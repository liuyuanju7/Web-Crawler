package com.liuyj.music.service;

import com.baomidou.mybatisplus.service.IService;
import com.liuyj.music.entity.Song;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liuyuanju1
 * @since 2018-08-09
 */
public interface ISongService extends IService<Song> {

    public List<Song> getRankSong();
}
