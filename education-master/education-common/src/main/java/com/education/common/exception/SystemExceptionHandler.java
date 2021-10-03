package com.education.common.exception;


import com.education.common.utils.Result;
import com.education.common.utils.ResultCode;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 系统全局异常处理类
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/10 10:53
 */
@ControllerAdvice
public class SystemExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(SystemExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result resolveException(Exception e) {
        Result result = new Result(ResultCode.FAIL, ResultCode.DEFAULT_FAIL_MESSAGE);
        if (e instanceof BusinessException) {
            BusinessException businessException = (BusinessException) e;
            if (businessException.getResultCode() != null) {
                result.setCode(businessException.getResultCode().getCode());
                result.setMessage(businessException.getResultCode().getMessage());
            }
            // 无权访问时shiro抛出的异常
        } else if (e instanceof UnauthorizedException) {
            result.setCode(ResultCode.NO_URL_PERMISSION);
            result.setMessage("权限不足, 无法访问");
        }
        logger.error("系统异常", e);
        return result;
    }
}
