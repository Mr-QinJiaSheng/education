package com.education.common.utils;

/**
 * http 响应状态码
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/10 10:46
 */
public class ResultCode {

    public static final int SUCCESS = 1; //响应成功状态码
    public static final int FAIL = 0; //响应失败状态码
    public static final int NOT_AUTH = 1001; //用户未认证
    public static final int NO_URL_PERMISSION = 1002; // url无权限访问状态码
    public static final String DEFAULT_SUCCESS_MESSAGE = "操作成功";
    public static final String DEFAULT_FAIL_MESSAGE = "系统异常";

    private int code = SUCCESS;
    private String message;

    public ResultCode() {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResultCode(int code, String message) {

        this.code = code;
        this.message = message;
    }
}
