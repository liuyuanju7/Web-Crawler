package com.liuyj.core.result;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liuyuanju1
 * @date 2018/9/19
 * @description: API统一返回结果类
 */
@Data
@Accessors(chain = true)
public class Result {

    private int code;

    private String message;

    private Object data;

    Result(){}

    Result(int code, String message, Object data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    @Override
    public String toString(){
        return JSON.toJSONString(this);
    }
}
