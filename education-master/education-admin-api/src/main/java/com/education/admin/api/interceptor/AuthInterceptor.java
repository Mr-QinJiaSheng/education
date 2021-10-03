package com.education.admin.api.interceptor;

import com.education.common.interceptor.BaseInterceptor;
import com.education.common.model.JwtToken;
import com.education.common.utils.ObjectUtils;
import com.education.common.utils.Result;
import com.education.common.utils.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * token 拦截器校验
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/5/23 21:39
 */
@Component
public class AuthInterceptor extends BaseInterceptor {

    @Autowired
    private JwtToken jwtToken;

    /**
     * controller 方法执行前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        String target = getRequestUrl(request);
        System.out.println(target);
        String token = request.getHeader("token");
        String userId = jwtToken.parserToken(token, String.class);
        // token 不存在或者token 已失效
        if (ObjectUtils.isEmpty(token) || ObjectUtils.isEmpty(userId)) {
            renderJson(response, Result.fail(ResultCode.NOT_AUTH, "用户未认证"));
            return false;
        }
        return true;
    }
}
