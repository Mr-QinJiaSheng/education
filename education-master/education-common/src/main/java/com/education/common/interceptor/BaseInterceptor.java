package com.education.common.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.education.common.utils.RequestUtils;
import com.education.common.utils.Result;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 拦截器基类
 * @author zengjintao
 * @version 1.0
 * @create_at 2018/12/22 19:43
 */
public abstract class BaseInterceptor implements HandlerInterceptor {

    private static final String contentType = "application/json; charset=utf-8";

    protected void renderJson(HttpServletResponse response, Result result) {
        String dataJson = JSONObject.toJSONString(result);
        PrintWriter writer = null;
        try {
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType(contentType);
            writer = response.getWriter();
            writer.write(dataJson);
            writer.flush();
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getRequestUrl(HttpServletRequest request) {
        return RequestUtils.getRequestUrl(request);
    }


    /**
     * 获取json 参数值
     * @param request
     * @return
     */
    protected String readData(HttpServletRequest request) {
       return RequestUtils.readData(request);
    }
}
