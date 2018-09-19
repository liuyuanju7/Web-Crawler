package com.liuyj.core.result;

/**
 * @author liuyuanju1
 * @date 2018/5/19
 * @description:
 */
public enum ResultCode {
    SUCCESS(200),//成功
    REDIRECT(304),//跳转URL
    FAIL(400),//失败
    UNAUTHORIZED(401),//未认证（签名错误）
    NOT_FOUND(404),//接口不存在
    INTERNAL_SERVER_ERROR(500);//服务器内部错误

    public int code;

    ResultCode(int code) {
        this.code = code;
    }
}
