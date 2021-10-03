package com.education.common.interceptor;

import com.education.common.annotation.DisabledResubmit;
import com.education.common.utils.IpUtils;
import com.education.common.utils.ObjectUtils;
import com.education.common.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 限制表单重复提交拦截器
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/7/28 21:38
 */
@Component
public class DisabledResubmitInterceptor extends BaseInterceptor {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 执行接口方法之前判断请求是否重复提交
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        String ip = IpUtils.getAddressIp(request);
        String target = getRequestUrl(request); // 请求的url
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            DisabledResubmit disabledResubmit = handlerMethod.getMethod().getAnnotation(DisabledResubmit.class);
            if (ObjectUtils.isNotEmpty(disabledResubmit)) {
                String key = ip + ":" + target;

                // 当key 不存在的时候，redis 添加这个key,并返回true, key 已经存在的话，直接返回false
               boolean flag = redisTemplate.opsForValue().setIfAbsent(key, System.currentTimeMillis(),
                        disabledResubmit.timeOut(), TimeUnit.SECONDS);
               if (!flag) {
                   // 代表表单重复提交
                   if (ObjectUtils.isNotEmpty(disabledResubmit.message())) {
                       renderJson(response, Result.fail(disabledResubmit.errorCode(), disabledResubmit.message()));
                       return  false;
                   }
               }

            }
        }
        return true;
    }
}
