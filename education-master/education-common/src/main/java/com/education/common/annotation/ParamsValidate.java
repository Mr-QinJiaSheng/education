package com.education.common.annotation;

import java.lang.annotation.*;

/**
 * 参数校验注解
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/5/8 20:58
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamsValidate {
    Param[] params();

    ParamsType paramsType() default ParamsType.FORM_DATA;
}
