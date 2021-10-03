package com.education.common.utils;

/**
 * 对响应结果进行封装
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/10 10:48
 */
public class Result {

    private Object data;
    private int code;
    private String message;

    /**
     * 默认响应成功
     */
    public Result() {
        this.code = ResultCode.SUCCESS;
        this.message = ResultCode.DEFAULT_SUCCESS_MESSAGE;
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public Result(ResultCode resultCode, Object data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS;
    }

    /**
     * 响应成功
     * @param code
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result success(int code, String message, T data) {
        return new Result(code, message, data);
    }

    /**
     * 响应成功
     * @param code
     * @param message
     * @param <T>
     * @return
     */
    public static <T> Result success(int code, String message) {
        return new Result(code, message);
    }

    /**
     * 响应成功
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result success(T data) {
        return new Result(ResultCode.SUCCESS, ResultCode.DEFAULT_SUCCESS_MESSAGE, data);
    }

    /**
     * 响应失败
     * @param code
     * @param message
     * @return
     */
    public static Result fail(int code, String message) {
        return new Result(code, message);
    }

    /**
     * 响应失败
     * @return
     */
    public static Result fail() {
        return new Result(ResultCode.FAIL, ResultCode.DEFAULT_FAIL_MESSAGE);
    }

    public <T> T getData() {
        return (T) data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
        if (code == ResultCode.FAIL) {
            this.message = "数据请求失败";
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
