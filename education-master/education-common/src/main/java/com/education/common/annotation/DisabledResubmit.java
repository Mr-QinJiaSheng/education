package com.education.common.annotation;

import java.lang.annotation.*;

/**
 * 限制表单重复提交注解类
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/7/28 21:26
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisabledResubmit {

    int timeOut() default 5; // 如果五秒内重复请求某一接口，则视为表单的重复提交

    int errorCode() default 0;

    String message() default "";
}
