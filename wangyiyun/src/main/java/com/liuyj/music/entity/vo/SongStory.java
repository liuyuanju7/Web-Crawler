package com.liuyj.music.entity.vo;

import com.liuyj.music.entity.Comment;
import com.liuyj.music.entity.Song;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author liuyuanju1
 * @date 2018/9/16
 * @description: 歌曲故事Vo类
 */
@Data
@Accessors(chain = true)
public class SongStory implements Serializable{
    /** 歌曲 */
    private Song song;
    /** 评论列表 */
    private List<Comment> comments;
    /** 关键词 */
    private List<String> keywords;
}
