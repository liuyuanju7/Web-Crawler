package com.liuyj.music.service;

import com.baomidou.mybatisplus.service.IService;
import com.liuyj.music.entity.Comment;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liuyuanju1
 * @since 2018-08-10
 */
public interface ICommentService extends IService<Comment> {

    List<Comment> getCommentsBySongId(String songId);
}
