package com.education.common.annotation;

import java.lang.annotation.*;

/**
 * 日志注解类
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/6/21 16:10
 */
@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemLog {

    /**
     * 操作描述
     * @return
     */
    String describe();
}
