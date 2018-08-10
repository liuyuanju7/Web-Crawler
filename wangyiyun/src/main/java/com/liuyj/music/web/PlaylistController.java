package com.liuyj.music.web;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.liuyj.jsoup.Music163;
import com.liuyj.music.entity.Playlist;
import com.liuyj.music.enums.MusicType;
import com.liuyj.music.service.IPlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author liuyuanju1
 * @since 2018-08-10
 */
@RestController
@RequestMapping("/music/playlist")
public class PlaylistController {

    @Autowired
    private IPlaylistService playlistService;

    @GetMapping("/init")
    public String getPlayList(){
        List<Playlist> playlists = Music163.getPlayListByType(MusicType.Minyao,35,35);
        playlistService.insertBatch(playlists);
        return "初始化完成，共初始化" + playlists.size() + "条数据";
    }

    @GetMapping("")
    public List<Playlist> getPlaylistByType(@RequestParam String type){
       return playlistService.selectList(new EntityWrapper<Playlist>().eq("musicType",type));
    }
}
