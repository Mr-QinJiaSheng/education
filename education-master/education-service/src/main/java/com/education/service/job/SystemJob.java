package com.education.service.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/16 9:56
 */
public class SystemJob extends BaseJob {
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(now));
        System.out.println("执行任务：" + SystemJob.class.getSimpleName());
    }
}
