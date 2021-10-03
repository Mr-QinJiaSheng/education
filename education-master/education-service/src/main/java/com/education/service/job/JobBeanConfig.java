package com.education.service.job;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/16 9:57
 */
//@Configuration
public class JobBeanConfig {

    private static final String DEFAULT_GROUP = "default_group";

    @Bean
    public JobDetail systemJob() {
        return JobBuilder.newJob(SystemJob.class)
                .withIdentity(SystemJob.class.getSimpleName(), DEFAULT_GROUP).storeDurably().build();
    }

    @Bean
    public Trigger jobTrigger() {
       return TriggerBuilder.newTrigger().forJob(systemJob().getKey())
               .withIdentity(SystemJob.class.getSimpleName(), DEFAULT_GROUP)
                .startNow().withSchedule(CronScheduleBuilder.cronSchedule("*/5 * * * * ?")).build();
    }

}
