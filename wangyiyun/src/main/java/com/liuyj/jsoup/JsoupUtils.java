package com.liuyj.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

/**
 * @author liuyuanju1
 * @date 2018/8/7
 * @description:
 */
public class JsoupUtils {
    public static void main(String[] args) throws IOException{
        System.out.println("--start--");
        JsoupUtils.getNetworkImage("http://www.tooopen.com/img/87.aspx", "E://jsoup//img//");
        System.out.println("--end--");
    }
    public static void getNetworkImage(String url, String outPath) throws IOException{
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        FileOutputStream outputStream = null;
        Document document;
        Elements elements;
        try {
            document = Jsoup.connect(url).get();
            //获取网站图片
            elements = document.select("img[src]");
            //获取后缀为png和jpg的图片的元素集合
           // Elements pngs = document.select("img[src~=(?i)\\.(png|jpe?g)]")
            //循环读取写入
            for (Element img : elements) {
                String outImage = UUID.randomUUID().toString().replaceAll("-", "") + ".jpg";
                //获取图片链接 获取全路径
                URL imgUrl = new URL(img.attr("abs:src"));
                //通过 url 获取文件输入流
                inputStream = imgUrl.openConnection().getInputStream();
                //输入流 放入缓冲流  提升读写速度
                bis = new BufferedInputStream(inputStream);
                //按字节读取
                byte[] buf = new byte[1024];
                //生成输出文件
                outputStream = new FileOutputStream(outPath + outImage);
                //边读边写
                int size = 0;
                while ((size = bis.read(buf)) != -1) {
                    outputStream.write(buf, 0, size);
                }
                //刷新文件流
                outputStream.flush();
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            //释放资源 遵循先开后关原则
            if(outputStream != null){
                outputStream.close();
            }
            if(bis != null){
                bis.close();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
    }
}
