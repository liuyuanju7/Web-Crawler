package com.liuyj.core.result;

/**
 * @author liuyuanju1
 * @date 2018/9/19
 * @description:API返回结果 统一生成器
 */
public class ResultGenerator {
    private static final String DEFAULT_SUCCESS_MESSAGE = "成功";
    private static final String DEFAULT_FAIL_MESSAGE = "失败";

    public static Result result(){
        return new Result();
    }

    public static Result result(ResultCode code){
        return new Result().setCode(code);
    }

    public static Result result(ResultCode code,String message){
        return result(code).setMessage(message);
    }

    public static Result result(ResultCode code, String message, Object data){
       return  result(code, message).setData(data);
    }

    public static Result successResult(){
        return new Result().setCode(ResultCode.SUCCESS)
                            .setMessage(DEFAULT_SUCCESS_MESSAGE);
    }

    public static Result successResult(String message){
        return new Result().setCode(ResultCode.SUCCESS).setMessage(message);
    }

    public static Result successResult(Object data){
        return successResult().setData(data);
    }

    public static Result successResult(String message, Object data){
        return new Result().setCode(ResultCode.SUCCESS).setMessage(message).setData(data);
    }

    public static Result failResult(){
        return new Result().setCode(ResultCode.FAIL)
                .setMessage(DEFAULT_FAIL_MESSAGE);
    }

    public static Result failResult(String message){
        return new Result().setCode(ResultCode.FAIL).setMessage(message);
    }

    public static Result failResult(Object data){
        return successResult().setData(data);
    }

    public static Result failResult(String message, Object data){
        return new Result().setCode(ResultCode.FAIL).setMessage(message).setData(data);
    }

    public static Result failResult(ResultCode code,String message){
        return new Result().setCode(code).setMessage(message);
    }

    public static Result genRedirctResult(Object data) {
        return new Result()
                .setCode(ResultCode.REDIRECT)
                .setData(data);
    }
}
