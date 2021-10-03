package com.education.service.task;

import com.education.service.system.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志消息监听器
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/6/21 16:22
 */
@Component
public class LogTaskListener implements TaskListener {

    @Autowired
    private SystemLogService systemLogService;

    /**
     * 用来异步保存日志信息到system_log表
     * @param taskParam
     */
    @Override
    public void onMessage(TaskParam taskParam) {

        taskParam.put("create_date", new Date());
        long startTime = (long) taskParam.get("startTime");
        long nowTime = System.currentTimeMillis();
        long requestTime = nowTime - startTime; // 获取接口响应的时间
        taskParam.put("request_time", requestTime + "ms");
        taskParam.remove("startTime");
        // 调用save保存日志
        systemLogService.save(taskParam);
    }
}
