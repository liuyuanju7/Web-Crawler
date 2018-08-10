package com.liuyj.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

/**
 * @author liuyuanju1
 * @date 2018/8/6
 * @description: 爬取 智联招聘 北京 java 的工作岗位 并 输出为 html
 */
public class JsoupHtml {
    public static void main(String[] args) throws IOException{
        //jl=530 表示工作地点为北京  p=1 表示第一页 可以动态改变 页数
        String url = "https://sou.zhaopin.com/jobs/searchresult.ashx?" +
                "jl=530&kw=java&sm=0&sg=96dd6baa3514407c9639329afaac53dc&p=1";
        //输出 html 文件
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("E:\\workinfo.html")));
        writer.write("");
        //读取文件模板
        File demo = new File(JsoupHtml.class.getClassLoader().getResource("templates/workinfo-template.html").getFile());
        Document demoDoc = Jsoup.parse(demo,"utf-8");
        Element tbody = demoDoc.getElementById("workinfo");
        tbody.text("");
        //插入 列头
        Element theader = tbody.appendElement("tr");
        theader.appendElement("th").text("序号");
        theader.appendElement("th").text("职位名称");
        theader.appendElement("th").text("公司名称");
        theader.appendElement("th").text("职位月薪");
        theader.appendElement("th").text("工作地点");
        theader.appendElement("th").text("发布日期");

        //抓取网页的工作信息
        Document document = Jsoup.connect(url).get();
        Element content = document.getElementById("newlist_list_content_table");
        Elements zwmc = content.getElementsByClass("zwmc");
        Elements gsmc = content.getElementsByClass("gsmc");
        Elements zwyx = content.getElementsByClass("zwyx");
        Elements gzdd = content.getElementsByClass("gzdd");
        Elements gxsj = content.getElementsByClass("gxsj");

        for(int i=1; i< zwmc.size(); i++){
//            System.out.println( i + " " + zwmc.get(i).text() + "  " + gsmc.get(i).text()
//                + "  " + zwyx.get(i).text() + "  " + gxsj.get(i).text());
            //插入数据列
            Element tr = tbody.appendElement("tr");
            tr.appendElement("td").text(i+"");
            tr.appendElement("td").text(zwmc.get(i).text());
            tr.appendElement("td").text(gsmc.get(i).text());
            tr.appendElement("td").text(zwyx.get(i).text());
            tr.appendElement("td").text(gzdd.get(i).text());
            tr.appendElement("td").text(gxsj.get(i).text());
        }
        writer.write(demoDoc.html());
        writer.close();
    }
}
