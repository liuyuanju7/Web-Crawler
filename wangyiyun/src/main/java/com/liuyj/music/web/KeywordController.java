package com.liuyj.music.web;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.liuyj.core.result.Result;
import com.liuyj.core.result.ResultGenerator;
import com.liuyj.music.entity.Comment;
import com.liuyj.music.entity.Keyword;
import com.liuyj.music.service.ICommentService;
import com.liuyj.music.service.IKeywordService;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author liuyuanju1
 * @since 2018-09-16
 */
@RestController
@RequestMapping("/music/keyword")
public class KeywordController {
    private static final Logger logger = LoggerFactory.getLogger(KeywordController.class);
    @Autowired
    private ICommentService commentService;
    @Autowired
    private IKeywordService keywordService;

    @GetMapping("/init")
    public Result initSongCommentKeyword(){
        // key 歌曲id val：所有评论内容
        Map<String,StringBuilder> commentMap = Maps.newHashMap();
        List<Comment> comments = commentService.selectList(new EntityWrapper<Comment>());
        if(CollectionUtils.isEmpty(comments)){
            logger.info("未查询到歌曲评论");
            return ResultGenerator.failResult("未查询到歌曲评论");
        }
        comments.stream().forEach(comment -> {
            String songId = comment.getSongId();
            //空行 会导致分词的 空指针异常
            String str = comment.getContent().replaceAll("\n","");
            if(commentMap.keySet().contains(songId)){
                commentMap.get(songId).append(str).append("\n");
            }else {
                StringBuilder builder = new StringBuilder();
                builder.append(str).append("\n");
                commentMap.put(songId,builder);
            }
        });
        long start = System.currentTimeMillis();
        logger.info("开始对所有歌曲的评论进行关键词分词");
        List<Keyword> keywordList = new ArrayList<>(commentMap.size());
        commentMap.entrySet().forEach(entry -> {
            List<Word> seg = WordSegmenter.seg(entry.getValue().toString(), SegmentationAlgorithm.MaxNgramScore);
            //分词排序  只取前10
            String keywords = converSegWords(seg);
            keywordList.add(new Keyword(entry.getKey(),keywords));
        });
        long end = System.currentTimeMillis();
        logger.info("所有歌曲的评论进行关键词分词结束，共处理{}首歌曲，共耗时{}s",keywordList.size(),(end-start)/1000);
        keywordService.insertBatch(keywordList);

        return ResultGenerator.successResult("歌曲对应评论分词结束");
    }

    private String converSegWords(List<Word> words){
        if(CollectionUtils.isEmpty(words)){
            return "";
        }
        Map<String,Integer> wordRankMap = Maps.newHashMap();
        words.forEach(word -> {
            String str  = word.getText();
            if(wordRankMap.keySet().contains(str)){
                wordRankMap.put(str,wordRankMap.get(str) + 1);
            }else{
                wordRankMap.put(str,1);
            }
        });
        List<Map.Entry<String,Integer>> wordRankList = Lists.newArrayList();
        wordRankList.addAll(wordRankMap.entrySet());
        StringBuilder builder = new StringBuilder();
        wordRankList = wordRankList.stream().sorted((o1, o2) -> o2.getValue() - o1.getValue()).limit(10).collect(Collectors.toList());
        wordRankList.forEach(e ->{
            builder.append(e.getKey()).append("-");
        });
        builder.deleteCharAt(builder.length()-1);

        return builder.toString();
    }
}
