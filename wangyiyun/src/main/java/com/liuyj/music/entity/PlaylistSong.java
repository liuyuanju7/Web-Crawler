package com.liuyj.music.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
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
 * @since 2018-08-14
 */
@Data
@ToString
@Accessors(chain = true)
@TableName("playlist_song")
public class PlaylistSong extends Model<PlaylistSong> {

    private static final long serialVersionUID = 1L;

	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 歌单id
     */
	@TableField("playlistId")
	private String playlistId;
    /**
     * 歌曲id
     */
	@TableField("songId")
	private String songId;
    /**
     * 歌名
     */
    @TableField("songName")
	private String songName;
    /**
     * 歌手
     */
	private String singer;
    /**
     * 专辑
     */
	private String album;


	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
