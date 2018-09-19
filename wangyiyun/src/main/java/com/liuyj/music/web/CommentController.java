package com.liuyj.music.web;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.liuyj.jsoup.Music163;
import com.liuyj.music.entity.Comment;
import com.liuyj.music.service.ICommentService;
import org.apdplat.word.WordSegmenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
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

    //初始化 排行前20歌曲的评论，
    @GetMapping("/rank")
    public String initRankSongComment(){
        List<String> songIds = commentService.getSongIdsByCounts();
        List<String> initSongId = Lists.newArrayList();
        songIds.forEach(id -> {
            List<Comment> commentList = commentService.selectList(new EntityWrapper<Comment>().eq("songId", id));
            if(CollectionUtils.isEmpty(commentList)){
                initSongId.add(id);
            }
        });
        if(!CollectionUtils.isEmpty(initSongId)){
            List<Comment> commentList = Lists.newArrayList();
            initSongId.forEach(songId -> {
                try {
                    List<Comment> comments = Music163.getCommentsBySongId(songId);
                    commentList.addAll(comments);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            commentService.insertBatch(commentList);
        }
        return "排行榜评论加载完成";
    }

    @GetMapping("/keyword/{songId}")
    public String segCommentToKeyword(@PathVariable String songId) throws Exception {
        List<Comment> comments = commentService.selectList(new EntityWrapper<Comment>().eq("songId",songId));
        StringBuilder builder = new StringBuilder();
        comments.forEach(comment -> {
            String str = comment.getContent().replaceAll("\n","");
            builder.append(str).append("\n");
        });
        //对评论进行分词
        File keyword = new File("e:\\keyword.txt");
        File comment = new File("e:\\comment.txt");
        PrintWriter pw = new PrintWriter(new FileWriter(comment));
        pw.println(builder.toString());
        pw.flush();
        pw.close();
        WordSegmenter.seg(comment,keyword);

        return "歌曲对应评论分词结束";
    }

}
