package com.liuyj.music.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author liuyuanju1
 * @since 2018-08-10
 */
@Data
@ToString
@Accessors(chain = true)
public class Playlist extends Model<Playlist> {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    /**
     * 歌单id
     */
    @TableField("listId")
    private String listId;
    /**
     * 歌单名称
     */
    private String name;
    /**
     * 音乐类型
     */
    @TableField("musicType")
    private String musicType;
    /**
     * 创建人
     */
    private String creater;
    /**
     * 歌单图片url
     */
    @TableField("imgUrl")
    private String imgUrl;
    /**
     * 播放量
     */
    private String count;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
