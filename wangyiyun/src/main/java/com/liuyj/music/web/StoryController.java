package com.liuyj.music.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.liuyj.music.entity.Comment;
import com.liuyj.music.entity.Keyword;
import com.liuyj.music.entity.Song;
import com.liuyj.music.entity.vo.SongStory;
import com.liuyj.music.service.ICommentService;
import com.liuyj.music.service.IKeywordService;
import com.liuyj.music.service.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author liuyuanju1
 * @date 2018/9/16
 * @description:
 */
@CrossOrigin
@RestController
@RequestMapping("/music/story")
public class StoryController {
    @Autowired
    private ICommentService commentService;
    @Autowired
    private ISongService songService;
    @Autowired
    private IKeywordService keywordService;

    @GetMapping("/rank")
    public List<SongStory> getRankSongStorys(){
        List<Song> songList = songService.getRankSong();
        List<Song> distinctSongList = Lists.newArrayList();
        Set<String> songIdsSet = Sets.newHashSet();
        songList.forEach(song -> {
            String songId = song.getSongId();
            if(!songIdsSet.contains(songId)){
                songIdsSet.add(songId);
                distinctSongList.add(song);
            }
        });
        songList = distinctSongList.stream().limit(3).collect(Collectors.toList());
        List<SongStory> songStories = Lists.newArrayList();

        songList.forEach(song -> {
            SongStory songStory = new SongStory();
            songStory.setSong(song);
            Keyword keyword = keywordService.selectOne(new EntityWrapper<Keyword>().eq("songId",song.getSongId()));
            songStory.setKeywords(convertKeywords(keyword));
            songStory.setComments(commentService.selectList(new EntityWrapper<Comment>().eq("songId",song.getSongId())));
            songStories.add(songStory);
        });

        return songStories;
    }

    /**
     * 将关键词文本转成 词组 并且剔除 一个字的 词
     * @param keyword
     * @return
     */
    private List<String> convertKeywords(Keyword keyword){
        if(keyword == null){
            return null;
        }
        String wordsStr = keyword.getWords();
        if(!StringUtils.isEmpty(wordsStr)){
            List<String> words = Arrays.asList(wordsStr.split("-"));
         //   return words.stream().filter(e -> e.length() > 1).collect(Collectors.toList());
            return  words;
        }

        return null;
    }
}
