package com.education.admin.api.controller;

import com.education.common.annotation.DisabledResubmit;
import com.education.common.annotation.SystemLog;
import com.education.common.base.BaseController;
import com.education.common.model.Captcha;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 图片验证码接口
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/25 17:02
 */
@RestController
public class ImageController extends BaseController {

    /**
     * 生成验证码
     * @param request
     * @param response
     */
    @GetMapping("/image")
    @SystemLog(describe = "生成验证码")
    public void image(HttpServletRequest request, HttpServletResponse response) {
        String key = request.getParameter("key");
        Captcha captcha = new Captcha(redisCacheBean, key);
        captcha.render(response);
    }


    @GetMapping("test")
    @DisabledResubmit(message = "fail")
    public String test() {
        return "success";
    }
}
