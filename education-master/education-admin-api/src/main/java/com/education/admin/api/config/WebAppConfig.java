package com.education.admin.api.config;

import com.education.admin.api.interceptor.AuthInterceptor;
import com.education.common.interceptor.DisabledResubmitInterceptor;
import com.education.common.interceptor.LogInterceptor;
import com.education.common.interceptor.ParamsValidateInterceptor;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * spring mvc系统配置类
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/5/10 10:04
 */
@Configuration
public class WebAppConfig implements WebMvcConfigurer {

    @Autowired
    private ParamsValidateInterceptor paramsValidateInterceptor;

    @Autowired
    private AuthInterceptor authInterceptor;
    @Autowired
    private LogInterceptor logInterceptor;

    @Autowired
    private DisabledResubmitInterceptor disabledResubmitInterceptor;

    @Value("${file.upload}")
    String uploadPath;

    private static final List<String> noInterceptorUrl = new ArrayList() {
        {
            add("/login");
            add("/image");
            add("/uploads/**");
            add("/ueditor/**");
        }
    };

    /**
     * 配置静态资源路径
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 表示以/uploads 开头的所有url 全部指向本地磁盘 uploadPath
        // 文件上传的虚拟路径
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + uploadPath);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(paramsValidateInterceptor);
        registry.addInterceptor(logInterceptor);
        registry.addInterceptor(disabledResubmitInterceptor);
     //   registry.addInterceptor(authInterceptor)
     //           .excludePathPatterns(noInterceptorUrl).addPathPatterns("/**");
    }
}
