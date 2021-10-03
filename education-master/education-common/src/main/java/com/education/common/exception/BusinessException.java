package com.education.common.exception;


import com.education.common.utils.ResultCode;

/**
 * 系统业务异常类
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/10 10:50
 */
public class BusinessException extends RuntimeException {

    private ResultCode resultCode;

    public BusinessException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Throwable throwable) {
        super(throwable);
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public BusinessException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
