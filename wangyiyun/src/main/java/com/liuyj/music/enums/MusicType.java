package com.liuyj.music.enums;

/**
 * @author liuyuanju1
 * @date 2018/8/10
 * @description:
 */
public enum MusicType {

 //   Minyao("民谣");
    Huayu("华语");
//    Liuxing("流行"),
//    Yaogun("摇滚"),
//    Dianzi("电子"),
//    Shuochang("说唱");


    private String type;

    MusicType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }
}
