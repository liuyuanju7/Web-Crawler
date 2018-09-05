package com.liuyj.music.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.liuyj.jsoup.Music163;
import com.liuyj.music.entity.Comment;
import com.liuyj.music.entity.Playlist;
import com.liuyj.music.entity.PlaylistSong;
import com.liuyj.music.entity.Song;
import com.liuyj.music.enums.MusicType;
import com.liuyj.music.service.ICommentService;
import com.liuyj.music.service.IPlaylistService;
import com.liuyj.music.service.IPlaylistSongService;
import com.liuyj.music.service.ISongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author liuyuanju1
 * @date 2018/8/14
 * @description:
 */
@RestController
@RequestMapping("/system")
public class IndexController {

    //获取 待初始化 歌曲类型的 前35个歌单

    //获取每个歌单的 基本信息 及 歌曲信息

    //获取 歌曲的 热评信息

    private Logger logger = LoggerFactory.getLogger(IndexController.class);
    //线程池
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    @Autowired
    private IPlaylistService playlistService;
    @Autowired
    private ISongService songService;
    @Autowired
    private IPlaylistSongService playlistSongService;
    @Autowired
    private ICommentService commentService;

    @RequestMapping("/init/song")
    public void intiMusicData() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        logger.info("开始初始化网易云歌曲相关信息--");

        //获取歌单信息 key 歌曲类型 val 歌单集合
        Map<String, List<Playlist>> playlistMap = getPlaylistMap();
        //歌单 入库
        List<Playlist> playlists = Lists.newArrayList();
        playlistMap.values().forEach(list -> playlists.addAll(list));
        playlistService.insertBatch(playlists);

        //获取歌单对应的歌曲id集合 key 歌单id val 歌曲id set
        Map<String, Set<String>> playlistSongMap = playlistSongMap(playlistMap);

        // 获取 对应歌单 具体 歌曲信息
        Map<String, List<Song>> songsMap = getSongsMap(playlistSongMap);

        //根据歌单具体歌曲信息 转换 歌单-歌曲基本信息 对照集合  方便歌单歌曲展示
        List<PlaylistSong> playlistSongs = convertPlaylistSongs(songsMap);
        // 歌单-歌曲 信息入库
        playlistSongService.insertBatch(playlistSongs);
        //歌曲入库
        List<Song> songList = Lists.newArrayList();
        songsMap.values().forEach(list -> songList.addAll(list));
        //将歌曲去重 插入数据库
        List<Song> distinctSongList = Lists.newArrayList();
        Set<String> songIdsSet = Sets.newHashSet();
        songList.forEach(song -> {
            String songId = song.getSongId();
            if(!songIdsSet.contains(songId)){
                songIdsSet.add(songId);
                distinctSongList.add(song);
            }
        });
        songService.insertBatch(distinctSongList);

        long end = System.currentTimeMillis();
        logger.info("初始化网易云歌曲相关信息结束--,共耗时{}s",(end-start)/1000);
    }

    @RequestMapping("/init/comment")
    public void intiMusciComment(){
        //获取数据库中的 所有歌曲id 去重
        Set<String> songIds = songService.selectList(new EntityWrapper<>())
                                .stream().map(Song::getSongId).collect(Collectors.toSet());
        // 根据歌曲id 获取对应评论
        List<Comment> commentList = getSongComment(songIds);
        // 评论入库
        commentService.insertBatch(commentList);
    }

    private Map<String,List<Playlist>> getPlaylistMap(){
        Map<String,List<Playlist>> playlistMap = Maps.newHashMap();
        for(MusicType type : MusicType.values()){
            playlistMap.put(type.getType(),Music163.getPlayListByType(type,null,null));
        }
        return playlistMap;
    }

    private Map<String,Set<String>> playlistSongMap( Map<String, List<Playlist>> playlistMap){
        List<Callable<Map<String,Set<String>>>> callables = Lists.newArrayList();
        playlistMap.entrySet().forEach(entry -> {
            Callable<Map<String,Set<String>>> callable = new Callable<Map<String, Set<String>>>() {
                @Override
                public Map<String, Set<String>> call() throws Exception {
                    Map<String,Set<String>> playlistSongMap = Maps.newHashMap();
                    entry.getValue().forEach(playlist -> {
                        Set<String> songIds = Music163.getPlaylistSongIds(playlist.getListId()).keySet();
                        playlistSongMap.put(playlist.getListId(),songIds);
                    });
                    return playlistSongMap;
                }
            };
            callables.add(callable);
        });
        Map<String,Set<String>> playlistSongMap = Maps.newHashMap();
        long start = System.currentTimeMillis();
        logger.info("开始获取歌单信息--");
        try {
            executor.invokeAll(callables).forEach(future -> {
                try {
                    playlistSongMap.putAll(future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException e) {
            logger.error("获取歌单线程中断",e);
        }
        long end = System.currentTimeMillis();
        logger.info("获取歌单信息结束:共获取了"+playlistSongMap.size()+"个歌单信息,耗时：" + (end-start)/1000 + "s");
        return playlistSongMap;
    }


    private Map<String,List<Song>> getSongsMap(Map<String, Set<String>> playlistSongMap){
        List<Callable<Map<String,List<Song>>>> songCallables = Lists.newArrayList();
        playlistSongMap.entrySet().forEach(entry -> {
            if(entry.getValue().size() > 500){
                //如果一个歌单歌曲数大于300，就不获取了，容易超时
                logger.warn("歌单{}歌曲数为{}，跳过此歌单",entry.getKey(),entry.getValue().size());
                return;
            }
            Callable<Map<String,List<Song>>> callable = new Callable<Map<String, List<Song>>>() {
                @Override
                public Map<String, List<Song>> call() throws Exception {
                    Map<String, List<Song>> result = Maps.newHashMap();
                    List<Song> songs = Music163.getSongsByPlaylistId(entry.getKey(),entry.getValue());
                    result.put(entry.getKey(),songs);
                    return result;
                }
            };
            songCallables.add(callable);
        });
        // key 歌单id  val 歌曲集合
        Map<String,List<Song>> songsMap = Maps.newHashMap();
        long start1 = System.currentTimeMillis();
        logger.info("开始获取歌单的歌曲信息--");
        try {
            executor.invokeAll(songCallables).forEach(future -> {
                try {
                    songsMap.putAll(future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException e) {
            logger.error("获取歌单歌曲线程中断",e);
        }
        long end1 = System.currentTimeMillis();
        logger.info("获取歌单的歌曲信息结束:共获取了"+songsMap.size()+"个歌单的歌曲信息,耗时：" + (end1-start1)/1000 + "s");

        return songsMap;

    }

    private List<PlaylistSong> convertPlaylistSongs(Map<String,List<Song>> songsMap){
        long start = System.currentTimeMillis();
        logger.info("开始转换歌单-歌曲 关联信息--");
        List<PlaylistSong> playlistSongs = Lists.newArrayList();
        songsMap.entrySet().stream().forEach(entry -> {
            entry.getValue().forEach(song -> {
                PlaylistSong playlistSong = new PlaylistSong();
                playlistSong.setPlaylistId(entry.getKey())
                        .setSongId(song.getSongId()).setSongName(song.getName())
                        .setSinger(song.getSinger()).setAlbum(song.getAlbum());

                playlistSongs.add(playlistSong);
            });
        });
        long end = System.currentTimeMillis();
        logger.info("转换歌单-歌曲 关联信息结束，共转换{}首歌曲,耗时：{}s",playlistSongs.size(),(end-start)/1000);
        return playlistSongs;
    }

    private List<Comment> getSongComment(Set<String> songIds){

        List<Callable<List<Comment>>> callables = Lists.newArrayList();
        songIds.forEach(songId -> {
            Callable<List<Comment>> callable = new Callable<List<Comment>>() {
                @Override
                public List<Comment> call() throws Exception {
                    List<Comment> comments = Music163.getCommentsBySongId(songId);
                    return comments;
                }
            };
            callables.add(callable);
        });
        long start = System.currentTimeMillis();
        logger.info("开始获取歌曲评论信息--");
        List<Comment> commentList = Lists.newArrayList();
        try {
            executor.invokeAll(callables).forEach(future -> {
                try {
                    commentList.addAll(future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException e) {
            logger.error("获取歌曲评论线程中断",e);
        }
        long end = System.currentTimeMillis();
        logger.info("获取歌曲评论信息结束:共获取了"+songIds.size()+"首歌曲，共计"+ commentList.size() +"条评论信息,耗时：" + (end-start)/1000 + "s");
        return commentList;
    }

}
