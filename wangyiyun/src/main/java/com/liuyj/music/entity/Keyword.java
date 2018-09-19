package com.liuyj.music.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * <p>
 * 歌曲 关键词
 * </p>
 *
 * @author liuyuanju1
 * @since 2018-09-16
 */
public class Keyword extends Model<Keyword> {

    private static final long serialVersionUID = 1L;

	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 歌曲id
     */
    @TableField("songId")
	private String songId;
    /**
     * 歌曲对应关键词，多个关键词用 - 连接
     */
	private String words;

	public Keyword(){}

	public Keyword(String songId,String words){
		super();
		this.songId = songId;
		this.words = words;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSongId() {
		return songId;
	}

	public void setSongId(String songId) {
		this.songId = songId;
	}

	public String getWords() {
		return words;
	}

	public void setWords(String words) {
		this.words = words;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "Keyword{" +
			", id=" + id +
			", songId=" + songId +
			", words=" + words +
			"}";
	}
}
