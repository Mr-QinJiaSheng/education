package com.education.admin.api;

import com.baidu.ueditor.ConfigManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/11 9:51
 */
@SpringBootApplication(scanBasePackages = "com.education")
@MapperScan("com.education.mapper")
public class EducationAdminApiApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(EducationAdminApiApplication.class);
        Environment environment = applicationContext.getEnvironment();
        String configFileName = environment.getProperty("ueditor.configFileName");
        ConfigManager.setConfigFileName(configFileName); // 设置富文本加载的配置文件
    }
}
