package com.liuyj.music.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.liuyj.music.entity.Comment;
import com.liuyj.music.mapper.CommentMapper;
import com.liuyj.music.service.ICommentService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liuyuanju1
 * @since 2018-08-10
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {
    private Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private static final String apiUrl = "http://music.163.com/api/v1/resource/comments/R_SO_4_";
    @Override
    public List<Comment> getCommentsBySongId(String songId) {
         String result = null;
        try {
            HttpGet httpGet = new HttpGet(apiUrl);
            httpGet.setHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpGet);
            result = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            logger.error("请求评论API失败:{}",e);
        }
        JSONObject jsonObject = JSON.parseObject(result);
        List<Object> comments = jsonObject.getJSONArray("hotComments");

        return null;
    }
}
