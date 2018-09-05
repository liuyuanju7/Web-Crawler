package com.liuyj.jsoup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.liuyj.jsoup.proxy.IpEntity;
import com.liuyj.music.entity.Comment;
import com.liuyj.music.entity.Playlist;
import com.liuyj.music.entity.Song;
import com.liuyj.music.enums.MusicType;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author liuyuanju1
 * @date 2018/8/8
 * @description: 爬取网易云音乐 及  前三评论
 */
public class Music163 {
    private static Logger logger = LoggerFactory.getLogger(Music163.class);

    private static final String playListUrl = "https://music.163.com/discover/playlist/?order=hot&cat=";
    private static final String commentApiUrl = "http://music.163.com/api/v1/resource/comments/R_SO_4_";

    private static String ip;
    private static int port;
    private static List<IpEntity> ipList;
    private static int index = 0;
//    static {
//        ipList = IpProxy.getProxyIp();
//        logger.info("获取到公开ip的数量为{}",ipList.size());
//        IpEntity ipEntity = ipList.get(index++);
//        ip = ipEntity.getIp();
//        port = ipEntity.getPort();
//
//    }
    /**
     * 根据歌曲类型获取歌单
     * @param type 歌曲类型
     * @param limit 显示条数
     * @param offset 起始
     * @return
     */
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

    /**
     * 获取歌单的歌曲信息
     * @param listId 歌单id
     * @return
     */
    public static List<Song> getSongsByPlaylistId(String listId){
        if(Strings.isNullOrEmpty(listId)){
            logger.error("歌单id不能为空");
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
    public static List<Song> getSongsByPlaylistId(String listId,Set<String> songIds){
        List<Song> songList = Lists.newArrayList();

        long startTime = System.currentTimeMillis();
        logger.info("开始抓取歌单{}中的歌曲：",listId);
        songIds.forEach(songId ->{
            Song song = getSongById(songId);
            songList.add(song);
        });
        long endTime = System.currentTimeMillis();
        logger.info("抓取歌单{}中的歌曲结束，共抓取{}首歌曲，共耗时:{}s：",listId,songList.size(),(endTime-startTime)/1000);

        return songList;
    }
    /**
     * 获取歌曲信息
     * @param songId 歌曲id
     * @return
     */
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

    /**
     * 获取歌单的 歌曲id-url map
     * @param listId
     * @return
     */
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
            logger.info("获取歌单：{}，共获取到{}首歌曲",listId,songsMap.size());
            return songsMap;
        }else{
            logger.info("获取歌单：{}失败，未获取到歌曲");
        }
        return null;
    }

    private static String getPlayListUrl(MusicType type,Integer limit,Integer offset){
        if(limit == null){
            limit = 35;
        }
        if(offset == null){
            offset = 0;
        }
        //默认查询 第一页 每页35条 -> 36
        return playListUrl.concat(type.getType()).concat("&limit="+limit).concat("&offset="+offset);
    }

    /**
     * 获取歌曲的热评
     * @param songId
     * @return
     * @throws ParseException
     */
    public static List<Comment> getCommentsBySongId(String songId) throws ParseException {
        String result = null;
        try {
            HttpGet httpGet = new HttpGet(commentApiUrl.concat(songId));
            httpGet.setHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpGet);
            result = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            logger.error("请求评论API失败:{}",e);
        }

        JSONObject jsonObject = JSON.parseObject(result);
        List<Object> comments = jsonObject.getJSONArray("hotComments");
        List<Comment> commentList = Lists.newArrayList();
        Comment comment = null;
        for(int i=0; i<comments.size(); i++){
            JSONObject elem = (JSONObject) comments.get(i);
            comment = new Comment();
            comment.setSongId(songId);
            comment.setContent(elem.getString("content"));
            comment.setCommentId(elem.getInteger("commentId"));
            comment.setLikeCount(elem.getInteger("likedCount"));
            comment.setTime(Instant.ofEpochMilli(Long.valueOf(elem.getString("time"))));
            JSONObject user = elem.getJSONObject("user");
            comment.setNickname(user.getString("nickname"));
            comment.setUserId(user.getInteger("userId"));
            comment.setAvatarUrl(user.getString("avatarUrl"));
            commentList.add(comment);
        }
        return commentList;
    }

    private static Document getDocument(String url){
        Connection.Response response = getResponse(url);

        Document document = null;
        try {
            document = response.parse();
        } catch (IOException e) {
            logger.error("url:{}转换为文档异常:{}",url,e);
        }
        return document;
    }

    private static Connection.Response getResponse(String url) {
        Connection.Response response = null;
        try {
            response = Jsoup.connect(url)
                    .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Safari/537.36")
                    .header("Cache-Control", "no-cache")
                    .referrer("https://music.163.com/")
                    .header("Cookie","_ntes_nnid=046ae2dab4a9cb2c35146c56ca45926a,1529301326636; _ntes_nuid=046ae2dab4a9cb2c35146c56ca45926a; usertrack=ezq0pVsp/d6xrSTTDh/DAg==; _ga=GA1.2.1120563217.1529478627; _iuqxldmzr_=32; Province=010; City=010; __gads=ID=cda162e8d7930f14:T=1533093923:S=ALNI_MYKqQk_ppA8TwINbDs4-t28E_aKYw; vinfo_n_f_l_n3=b946c5fef8451e2a.1.0.1533093917490.0.1533093931133; __utmc=94650624; WM_TID=2dOSQOzJs8Gj2UETHYVBm7G3nqbryOTR; MUSIC_U=f09b1d9c536588a2b817aca47593dff15887c94eedf999601921218bf37947149c5bc1352321fe1643eeb217318c26349c3a30d5b950ffe5cf07cde5e6fd63271e318fe5f8316e09bf122d59fa1ed6a2; __remember_me=true; __csrf=745d75b4fc4eab65a9cbae31c4946503; __utmz=94650624.1534212326.19.8.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); WM_NI=bIiZ5%2FkGl%2BTipo1Ts0p4QOIAG2ttkKJylbnmcc4O75ZXYbeSybmZuu90Zb74BqE1DAx9V2%2F3LqmxQSlQ05jIfaHfW29aukRB%2B%2BGFRQF9GAeLuv7EJr8m8HzgdLW86ULvRk8%3D; WM_NIKE=9ca17ae2e6ffcda170e2e6eed4ef80a8a8838fc447979ec094ca7aabbba291cf398be8af96db7981eca883f52af0fea7c3b92aa297a7bbb825a999a4ccd763b4b1ffb5f23cb4ec9fb0cc4b85eebc95ea418aa7a483ed67a1bdfcb0b63f9a889783e859bbbea3bbf87ffbf09683b64082af8bd2c74bbce79ad3ae3990afa399d459b08f00d6e943a6bf8887d63afbbfbb8cd14ae9f589a7d15b86e789a2e848b7aaf9a6c97afcb3f8a9f96da89499d9b55f90aa9e8cbb37e2a3; JSESSIONID-WYYY=yE%2BnYoRZ8SyYuRD27Us9vrRvcYOj05cnR%2Bcu94dFwc%2BxNkok0pBOaQnQluA1QpgJBEFvj9nZblwrNaSdaib6Ma1%2FBgEFDn%2BNnHIqWfc9893nWYVg%2FZNUDIImIA%2F3S74ys6zA8cQdpOuI7V956vgRPOwrR9K5E65%2FjePIlSXOuKEi52FB%3A1534295243820; __utma=94650624.1120563217.1529478627.1534212326.1534293912.20; __utmb=94650624.5.10.1534293912")
                    .timeout(2000000000)
                   // .proxy(ip,port)
                    .execute();

        } catch (IOException e) {
            logger.error("获取Url:{}链接失败!{}",url,e.getStackTrace());
//            IpEntity ipEntity = ipList.get(index++);
//            ip = ipEntity.getIp();
//            port = ipEntity.getPort();
//            logger.warn("重新设置代理ip:{},端口：{}",ip,port);
//            return getResponse(url);
        }
        return response;
    }

    public static void getwangyiyun() throws IOException{
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

        //输出 html 文件
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("E:\\my_github\\httpclient\\http-client\\src\\main\\resources\\output\\musicList.html")));
        writer.write("");

        File input = new File(Music163.class.getClassLoader().getResource("templates/music.html").getFile());
        Document template = Jsoup.parse(input,"utf-8");
        Element table = template.getElementById("music");
        Element tempTr = table.getElementById("template");
        tempTr.attr("id",id);
        tempTr.getElementsByClass("song").select("span").first().text(songName.text() + "--" + singer.attr("title"));
        tempTr.getElementsByClass("outchain").select("iframe").attr("src",outchain);
        tempTr.removeClass("hide");
        table.append(tempTr.html());
        writer.write(template.html());
        writer.close();

    }
}
