package com.liuyj.music.entity;

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
 * @since 2018-08-09
 */
@Data
@ToString
@Accessors(chain = true)
public class Song implements Serializable {

    private static final long serialVersionUID = 1L;

	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 歌曲id
     */
    @TableField("songId")
	private String songId;
    /**
     * 歌曲名
     */
	private String name;
	private String singer;
	@TableField("singerIds")
	private String singerIds;
    /**
     * 专辑
     */
	private String album;
    /**
     * 外链
     */
	@TableField("outChain")
	private String outChain;
	@TableField("commentCount")
	private Integer componentCount;
	@TableField("imgUrl")
	private String imgUrl;


}
