package com.liuyj.music.web;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.liuyj.jsoup.Music163;
import com.liuyj.music.entity.Song;
import com.liuyj.music.service.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author liuyuanju1
 * @since 2018-08-09
 */
@RestController
@RequestMapping("/music/song")
public class SongController {

    @Autowired
    private ISongService songService;

    @RequestMapping("/add")
    public void getSong() throws IOException {
       // Song song = Music163.getwangyiyun();
       // songService.insert(song);
    }

    @GetMapping("/init")
    public List<Song> initPlaylistSongs(){
        List<Song> songList = Music163.getSongsByPlaylistId("2355333774");
     //   songService.insertBatch(songList);
        return songList;
    }

    @GetMapping("/playlist/{listId}")
    public List<Song> getPlaylistSong(@PathVariable String listId){
        List<Song> songList = songService.selectList(new EntityWrapper<Song>().eq("listId",listId));
        return songList;
    }

    @GetMapping("/rank")
    public List<Song> getRankSongs(){
        List<Song> songs = songService.getRankSong();
        //因为之前获取 song 插入数据库未去重， 这里处理一下， 获取歌曲时的去重 已经加上
        List<Song> distinctSongList = Lists.newArrayList();
        Set<String> songIdsSet = Sets.newHashSet();
        songs.forEach(song -> {
            String songId = song.getSongId();
            if(!songIdsSet.contains(songId)){
                songIdsSet.add(songId);
                distinctSongList.add(song);
            }
        });
        return distinctSongList;
    }
}
