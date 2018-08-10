package com.liuyj.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * @author liuyuanju1
 * @date 2018/8/5
 * @description: 爬取知乎demo
 */
public class JsoupTest {
    public static void main(String[] args) throws IOException{
        // 知乎发现 页面url
        String targetUrl = "https://www.zhihu.com/explore/recommendations";
        // 模拟谷歌浏览器
        Document document = getDocumentByUrl(targetUrl);

        Element mainElem = document.getElementById("zh-recommend-list-full");
        Elements urlList = mainElem.select("div.zh-general-list")
                                    .select("a.question_link");
        urlList.forEach(url -> {
            //根据相对链接 转成完整链接
            String questionUrl = url.attr("abs:href");
            Document questionElem = getDocumentByUrl(questionUrl);
            //问题标题
            Elements title = questionElem.select("div.QuestionHeader")
                                            .select("h1.QuestionHeader-title");
            //问题描述
            Elements detail = questionElem.select("div.QuestionHeader-detail")
                                            .select("span");
            //问题回答 只取第一个
            Elements content = questionElem.select("div.Question-main")
                                            .select("div.Question-mainColumn")
                                            .select("div")
                                            .select("div.RichContent-inner")
                                            .select("span");

            //打印内容
            System.out.println("\n" + "问题链接：" + questionUrl);
            System.out.println("问题标题：" + title.text());
            System.out.println("问题描述：" + detail.text());
            System.out.println("高赞回答：" + content.text());
            System.out.println("------------------*****----------------");
        });
    }

    public static Document getDocumentByUrl(String url){
        Document document = null;
        try {
            document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                    .get();
        } catch (IOException e) {
            System.out.println("链接失败");
        }
        return document;
    }
}
