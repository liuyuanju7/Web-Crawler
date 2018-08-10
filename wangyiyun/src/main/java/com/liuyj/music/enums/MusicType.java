package com.liuyj.music.enums;

/**
 * @author liuyuanju1
 * @date 2018/8/10
 * @description:
 */
public enum MusicType {

    Minyao("民谣");

    private String type;

    MusicType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }
}
