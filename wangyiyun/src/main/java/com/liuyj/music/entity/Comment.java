package com.liuyj.music.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

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
public class Comment extends Model<Comment> {

    private static final long serialVersionUID = 1L;

	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 评论id
     */
	@TableField("commentId")
	private Integer commentId;
    /**
     * 用户id
     */
	@TableField("userId")
	private Integer userId;
    /**
     * 用户头像url
     */
	@TableField("avatarUrl")
	private String avatarUrl;
    /**
     * 用户昵称
     */
	@TableField("nickname")
	private String nickname;
    /**
     * 评论内容
     */
	private String content;
    /**
     * 评论时间
     */
	private Date time;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
