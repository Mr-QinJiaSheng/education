package com.education.service.task;

import com.alibaba.fastjson.JSONObject;
import com.education.common.annotation.SystemLog;
import com.education.common.constants.Constants;
import com.education.common.model.AdminUserSession;
import com.education.common.utils.IpUtils;
import com.education.common.utils.ObjectUtils;
import com.education.common.utils.RequestUtils;
import com.education.service.system.SystemAdminService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 日志切面
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/6/21 16:16
 */
//@Aspect
//@Component
public class LogAspect {

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private TaskManager taskManager;

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    /**
     * 拦截com.education.admin.api.controller下面的所有类中的所有接口
     * @param point
     * @return
     */
    @Around("execution(public * com.education.*.*.controller..*.*(..))")
    public Object invoke(ProceedingJoinPoint point) throws Throwable {

        long startTime = System.currentTimeMillis(); //获取接口请求的开始时间
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod(); // 获取controller目标方法
        SystemLog systemLog = method.getAnnotation(SystemLog.class);
        TaskParam taskParam = new TaskParam(LogTaskListener.class);
        try {
            if (ObjectUtils.isNotEmpty(systemLog)) {
                taskParam.put("operation_desc", systemLog.describe());
            }
            HttpServletRequest request = RequestUtils.getRequest();
            // 获取请求的url
            String targetUrl = RequestUtils.getRequestUrl(request);
            taskParam.put("request_url", targetUrl);
            String methodType = request.getMethod();
            String contentType = request.getHeader("Content-Type");
            Object params = null;
            if (ObjectUtils.isNotEmpty(contentType) &&
                    contentType.contains(Constants.JSON_CONTENT_TYPE)) {
                params = RequestUtils.readData(request);
            } else {
                params = request.getParameterMap();
                if (ObjectUtils.isNotEmpty(params)) {
                    StringBuilder content = new StringBuilder();
                    Map data = (Map)params;
                    data.keySet().forEach(key -> {
                        // request.getParameterMap() 获取的参数key所对应的值默认是数组类型
                        String values[] = request.getParameterValues((String) key);
                        if (values.length == 1) {
                            content.append(key + "=");
                            content.append(values[0]);
                        } else {
                            content.append(key + "[]={");
                            for (String value : values) {
                                content.append(value + ", ");
                            }
                            content.append("}");
                        }
                    });
                    params = content.toString();
                }
            }
            params = JSONObject.toJSONString(params);
            taskParam.put("params", params);
            taskParam.put("ip", IpUtils.getAddressIp(request));
            taskParam.put("startTime", startTime);
            taskParam.put("method", methodType);
            AdminUserSession adminUserSession = systemAdminService.getAdminUserSession();
            if (ObjectUtils.isNotEmpty(adminUserSession)) {
                taskParam.put("user_id", adminUserSession.getUserMap().get("id"));
            }
            Object result = point.proceed(); // 执行controller目标方法
            taskManager.pushTask(taskParam);
            return result;
        } catch (Throwable throwable) {
            StringBuffer error = new StringBuffer();
            StackTraceElement[] stackTraceElements = throwable.getStackTrace();
            for (StackTraceElement item : stackTraceElements) {
                error.append(item.toString() + " ");
            }
            taskParam.put("exception", error.toString());
            taskManager.pushTask(taskParam);
            logger.error(error.toString(), throwable);
            throw throwable;
        }

    }
}
