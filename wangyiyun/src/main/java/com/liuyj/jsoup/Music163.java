package com.liuyj.jsoup;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.liuyj.music.entity.Playlist;
import com.liuyj.music.entity.Song;
import com.liuyj.music.enums.MusicType;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author liuyuanju1
 * @date 2018/8/8
 * @description: 爬取网易云音乐 及  前三评论
 */
public class Music163 {
    private static Logger logger = LoggerFactory.getLogger(Music163.class);

    private static final String playListUrl = "https://music.163.com/discover/playlist/?order=hot&cat=";

    public static void main(String[] args) throws IOException{
      //  getMusicFrom163();
    //    getwangyiyun();
    }

    public static List<Playlist> getPlayListByType(MusicType type,Integer limit,Integer offset){
        //组装 url
        String url = getPlayListUrl(type,limit,offset);

        Document document = getDocument(url);

        Elements playListUl = document.getElementById("m-pl-container").select("li");

        if(!CollectionUtils.isEmpty(playListUl)){
            List<Playlist> playLists = Lists.newArrayList();
            Playlist playList = null;
            for(Element element : playListUl){
                String id = element.getElementsByClass("icon-play").attr("data-res-id");
                String name = element.getElementsByClass("msk").attr("title");
                String imgUrl = element.getElementsByTag("img").attr("src");
                String creater = element.getElementsByClass("nm").text();
                String count = element.getElementsByClass("nb").text();

                playList = new Playlist().setMusicType(type.getType())
                        .setListId(id).setName(name).setImgUrl(imgUrl).setCreater(creater).setCount(count);

                playLists.add(playList);
            }
            logger.info("获取到音乐类型为{}的歌单个数为{}", type.getType(),playLists.size());
            return playLists;
        }

        return null;
    }

    public static List<Song> getSongsByPlaylistId(String listId){
        if(Strings.isNullOrEmpty(listId)){
            logger.error("歌单idb不能为空");
        }

        Map<String, String> songIdMap = getPlaylistSongIds(listId);

        if(CollectionUtils.isEmpty(songIdMap)){
            logger.warn("未获取到歌单：{}中的曲目",listId);
            return null;
        }
        List<Song> songList = Lists.newArrayList();

        long startTime = System.currentTimeMillis();
        logger.info("开始抓取歌单{}中的歌曲：",listId);
        songIdMap.keySet().forEach(songId ->{
            Song song = getSongById(songId);
            songList.add(song);
        });
        long endTime = System.currentTimeMillis();
        logger.info("抓取歌单{}中的歌曲结束，共耗时:{}s：",listId,(endTime-startTime)/1000);

        return songList;
    }

    public static Song getSongById(String songId){
        String url = "http://music.163.com/m/song?id=" + songId;
        Document document = getDocument(url);
        //歌名
        Element songName = document.getElementsByClass("f-ff2").first();
        //歌手
        Element singer = document.getElementsByClass("s-fc4").select("span").get(0);
        //获取歌手id 可能多个
        Elements singers = singer.select("a");
        StringBuilder singerIds = new StringBuilder();
        for(Element element : singers){
            singerIds.append(element.attr("href").split("\\=")[1]).append("-");
        }
        //删除最后一个 -
        singerIds.deleteCharAt(singerIds.length()-1);
        //专辑
        Element album = document.getElementsByClass("s-fc4").get(1).select("a").first();
        // 获取歌曲播放外链
        String outchain = "http://music.163.com/outchain/player?type=2&id=" + songId + "&auto=1&height=66";

        //歌曲图片
        Element imgUrl = document.getElementsByClass("cvrwrap").select("img").first();
        return new Song().setSongId(songId).setName(songName.text())
                .setSinger(singer.attr("title")).setSingerIds(singerIds.toString()).setOutChain(outchain)
                .setAlbum(album.text()).setImgUrl(imgUrl.attr("abs:src"));
    }

    public static Map<String,String> getPlaylistSongIds(String listId){
        String url = "https://music.163.com/playlist?id=" + listId;

        Document document = getDocument(url);

        Elements songElems = document.getElementById("song-list-pre-cache").select("li");
        if(!CollectionUtils.isEmpty(songElems)){
            Map<String,String> songsMap = Maps.newHashMap();
            songElems.forEach(songLi -> {
                String songId = songLi.select("a").first().attr("href").split("\\=")[1];
                songsMap.put(songId,songLi.select("a").text());
            });
            return songsMap;
        }
        return null;
    }

    private static String getPlayListUrl(MusicType type,Integer limit,Integer offset){
        if(limit == null){
            limit = 35;
        }
        if(offset == null){
            offset = 35;
        }
        //默认查询 第一页 每页35条
        return playListUrl.concat(type.getType()).concat("&limit="+limit).concat("&offset="+offset);
    }

    private static Document getDocument(String url){
        Connection.Response response = null;
        try {
            response = Jsoup.connect(url)
                    .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Safari/537.36")
                    .header("Cache-Control", "no-cache")
                    .timeout(2000000000)
                    .execute();
        } catch (IOException e) {
            logger.error("获取Url:{}链接失败!：{}",url,e);
        }

        Document document = null;
        try {
            document = response.parse();
        } catch (IOException e) {
            logger.error("url:{}转换为文档异常:{}",url,e);
        }
        return document;
    }

    public static Song getwangyiyun() throws IOException{
        String id = "461080452";
        Connection.Response execute = Jsoup.connect("http://music.163.com/m/song?id=" + id)
                .header("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Safari/537.36")
                .header("Cache-Control", "no-cache").timeout(2000000000)
//							.proxy(IpProxy.ipEntitys.get(i).getIp(),IpProxy.ipEntitys.get(i).getPort())
                .execute();
        String body = execute.body();
        // System.out.println(body);
        Document parse = execute.parse();

        //歌名
        Element songName = parse.getElementsByClass("f-ff2").first();
        //歌手
        Element singer = parse.getElementsByClass("s-fc4").select("span").get(0);
        // 获取歌曲播放外链
        String outchain = "http://music.163.com/outchain/player?type=2&id=" + id + "&auto=1&height=66";
        System.out.println(songName.text() + " " + singer.attr("title") + " " + outchain);

//        //输出 html 文件
//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("E:\\my_github\\httpclient\\http-client\\src\\main\\resources\\output\\musicList.html")));
//        writer.write("");
//
//        File input = new File(Music163.class.getClassLoader().getResource("templates/music.html").getFile());
//        Document template = Jsoup.parse(input,"utf-8");
//        Element table = template.getElementById("music");
//        Element tempTr = table.getElementById("template");
//        tempTr.attr("id",id);
//        tempTr.getElementsByClass("song").select("span").first().text(song.text() + "--" + singer.attr("title"));
//        tempTr.getElementsByClass("outchain").select("iframe").attr("src",outchain);
//        tempTr.removeClass("hide");
//        table.append(tempTr.html());
//        writer.write(template.html());
//          writer.close();

        Song song = new Song();
        song.setSongId(id).setName(songName.text()).setSinger(singer.attr("title")).setOutChain(outchain);
        return song;

    }
}
