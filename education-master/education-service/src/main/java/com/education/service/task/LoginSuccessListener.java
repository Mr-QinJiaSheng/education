package com.education.service.task;

import com.education.service.system.SystemAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 监听用户登录成功消息
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/5/6 20:22
 */
@Component
public class LoginSuccessListener implements TaskListener {

    @Autowired
    private SystemAdminService systemAdminService;

    @Override
    public void onMessage(TaskParam taskParam) {
        systemAdminService.update(taskParam);
    }
}
