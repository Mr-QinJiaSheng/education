package com.education.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**bean 实例工具类
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/13 14:29
 */
@Component
@Lazy(false)
public class SpringBeanManager implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanManager.applicationContext = applicationContext;
    }

    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return (T) applicationContext.getBean(clazz);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
