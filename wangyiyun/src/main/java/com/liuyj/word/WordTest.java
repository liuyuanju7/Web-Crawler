package com.liuyj.word;

import org.apdplat.word.WordSegmenter;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * @author liuyuanju1
 * @date 2018/9/13
 * @description:
 */
public class WordTest {
    public static void main(String[] args) throws Exception {
        File keyword = new File("e:\\keyword.txt");
        File comment = new File("e:\\comment.txt");

        PrintWriter pw = new PrintWriter(new FileWriter(comment));
        pw.println("我爱慕容晓");
        pw.println("人间不值得");
        pw.flush();
        pw.close();
      //  Utils.seg(comment,keyword,false, SegmentationAlgorithm.MaximumMatching);
      //  WordSegmenter.seg(comment,keyword);
        WordSegmenter.seg("我爱慕容晓").forEach(e -> System.out.println(e));
    }
}
