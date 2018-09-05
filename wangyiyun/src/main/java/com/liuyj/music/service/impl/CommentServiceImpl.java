package com.liuyj.music.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.liuyj.music.entity.Comment;
import com.liuyj.music.mapper.CommentMapper;
import com.liuyj.music.service.ICommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liuyuanju1
 * @since 2018-08-10
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {
    private Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Override
    public List<String> getSongIdsByCounts() {
        List<Comment> commentList = baseMapper.selectList(new EntityWrapper<Comment>()
                .orderBy("likeCount", false)
                .groupBy("songId"));
        if(CollectionUtils.isEmpty(commentList)){
            logger.error("未查询到评论表排序情况");
            return null;
        }
        //只返回 排序前二十的 歌曲id 集合
        return commentList.stream().limit(20).map(Comment::getSongId).collect(Collectors.toList());
    }
}
