package com.liuyj.music.web;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.liuyj.core.result.Result;
import com.liuyj.core.result.ResultGenerator;
import com.liuyj.music.entity.Playlist;
import com.liuyj.music.service.IPlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author liuyuanju1
 * @since 2018-08-10
 */
@RestController
@CrossOrigin
@RequestMapping("/music/playlist")
public class PlaylistController {

    @Autowired
    private IPlaylistService playlistService;


    @GetMapping("")
    public Result getPlaylistByType(@RequestParam String type, @RequestParam(defaultValue = "0") int current,
                                    @RequestParam(defaultValue = "9")int pageSize){
        Page<Playlist> page = new Page<>(current,pageSize);
        Page<Playlist> playlistPage = playlistService.selectPage(page, new EntityWrapper<Playlist>().eq("musicType", type));
        return ResultGenerator.successResult(playlistPage);
    }
}
