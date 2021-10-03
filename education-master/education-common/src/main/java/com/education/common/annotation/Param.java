package com.education.common.annotation;

import java.lang.annotation.*;

/**
 * 参数校验注解
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/5/8 20:52
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

    int errorCode() default 0;
    String name(); //需要校验字段名称
    String message() default "";

    String regexp() default ""; // 正则表达式校验
    String regexpMessage() default ""; // 正则校验失败提示
}
