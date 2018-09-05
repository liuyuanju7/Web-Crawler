package com.liuyj.music.web;


import com.liuyj.jsoup.Music163;
import com.liuyj.music.entity.Comment;
import com.liuyj.music.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
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
@RequestMapping("/music/comment")
public class CommentController {

    @Autowired
    private ICommentService commentService;

	@GetMapping("/{songId}")
    public List<Comment> getCommentBySongId(@PathVariable String songId) throws ParseException {
        return Music163.getCommentsBySongId(songId);

    }

}
