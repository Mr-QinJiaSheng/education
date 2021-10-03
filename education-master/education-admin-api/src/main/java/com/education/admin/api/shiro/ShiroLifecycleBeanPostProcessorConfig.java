package com.education.admin.api.shiro;

import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/22 11:16
 */
@Configuration
public class ShiroLifecycleBeanPostProcessorConfig {

    /**
     * Shiro生命周期处理器
     * @return
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
}
