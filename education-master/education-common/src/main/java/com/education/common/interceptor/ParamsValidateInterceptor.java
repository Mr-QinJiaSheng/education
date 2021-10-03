package com.education.common.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.education.common.annotation.Param;
import com.education.common.annotation.ParamsType;
import com.education.common.annotation.ParamsValidate;
import com.education.common.utils.ObjectUtils;
import com.education.common.utils.RegexUtils;
import com.education.common.utils.Result;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Handler;

/**
 * 参数校验拦截器
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/5/8 21:08
 */
@Component
public class ParamsValidateInterceptor extends BaseInterceptor {

    /**
     * controller方法执行前调用
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod(); // 获取执行的目标方法
            ParamsValidate paramsValidate = method.getAnnotation(ParamsValidate.class);
            if (ObjectUtils.isNotEmpty(paramsValidate)) {
                return checkParam(request, response, paramsValidate);
            }
        }
        return true;
    }

    private boolean checkParam(HttpServletRequest request,
                               HttpServletResponse response, ParamsValidate paramsValidate) {
        Param[] params = paramsValidate.params();
        ParamsType paramsType = paramsValidate.paramsType();
        boolean isJsonData = false;
        Map dataMap = null;
        if (paramsType == ParamsType.JSON_DATA) {
            isJsonData = true;
            String data = readData(request);
            dataMap = JSONObject.parseObject(data);
        }
        for (Param param : params) {
            Object value = null;
            String name = param.name();
            if (isJsonData) {
                value = dataMap.get(name);
            } else {
                value = request.getParameter(name);
            }

            if (ObjectUtils.isEmpty(value)) {
                renderJson(response, Result.fail(param.errorCode(), param.message()));
                return false;
            } else {
                String regexp = param.regexp();
                if (ObjectUtils.isNotEmpty(regexp)) {
                    boolean regexFlag = RegexUtils.compile(regexp, value);
                    if (!regexFlag) {
                        renderJson(response, Result.fail(param.errorCode(), param.regexpMessage()));
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
