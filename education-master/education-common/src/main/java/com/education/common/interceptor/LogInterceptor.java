package com.education.common.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;


/**
 * 系统接口日志拦截器
 * @author zengjintao
 * @version 1.0
 * @create_at 2018/11/27 20:17
 */
@Component
public class LogInterceptor extends BaseInterceptor {

    private static final String title = "\nSpringMvc" + " action report -------- ";
    private static int maxOutputLengthOfParaValue = 512;

    private final Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    private static final ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    /**
     * 解决前端跨域问题
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //解决ajax跨域问题
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "*");
        response.addHeader("Access-Control-Allow-Headers", "*");
        response.addHeader("Access-Control-Max-Age", "3600");//30 分钟
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        String contentPath = request.getServletContext().getContextPath();
        int contentPathLength = contentPath.length();
        String target = request.getRequestURI();
        if (contentPathLength != 0) {
            target = target.substring(contentPathLength);
        }
        StringBuilder sb = new StringBuilder(title).append(sdf.get().format(new Date())).append(" --------------------------\n");
        sb.append("Url          : ").append(request.getMethod()).append(" ").append(target).append("\n");

        if (handler instanceof HandlerMethod) {
            sb.append("Controller   : ").append(((HandlerMethod) handler).getBean().getClass()).append("\n");
            sb.append("Method       : ").append(((HandlerMethod) handler).getMethod().getName()).append("\n");
        }

        if (modelAndView != null)
            sb.append("ViewName     : ").append(modelAndView.getViewName()).append("\n");

        Enumeration<String> e = request.getParameterNames();
        if (e.hasMoreElements()) {
            sb.append("Parameter    : ");
            while (e.hasMoreElements()) {
                String name = e.nextElement();
                String[] values = request.getParameterValues(name);
                if (values.length == 1) {
                    sb.append(name).append("=");
                    if (values[0] != null && values[0].length() > maxOutputLengthOfParaValue) {
                        sb.append(values[0].substring(0, maxOutputLengthOfParaValue)).append("...");
                    } else {
                        sb.append(values[0]);
                    }
                }
                else {
                    sb.append(name).append("[]={");
                    for (int i = 0; i < values.length; i++) {
                        if (i > 0)
                            sb.append(",");
                        sb.append(values[i]);
                    }
                    sb.append("}");
                }
                sb.append("  ");
            }
            sb.append("\n");
        }
        sb.append("--------------------------------------------------------------------------------\n");
        logger.info(sb.toString());
    }

    private void asyncSaveLog(HandlerMethod handlerMethod) {

    }
}
