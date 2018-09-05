package com.liuyj.music.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.liuyj.music.entity.Song;
import com.liuyj.music.mapper.SongMapper;
import com.liuyj.music.service.ICommentService;
import com.liuyj.music.service.ISongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liuyuanju1
 * @since 2018-08-09
 */
@Service
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements ISongService {
    private Logger logger = LoggerFactory.getLogger(SongServiceImpl.class);

    @Autowired
    private ICommentService commentService;
    @Override
    public List<Song> getRankSong() {
        List<String> songIds = commentService.getSongIdsByCounts();
        if(CollectionUtils.isEmpty(songIds)){
            logger.error("未获取到歌曲排行榜信息");
            return null;
        }

        return baseMapper.selectList(new EntityWrapper<Song>().in("songId",songIds));
    }
}
