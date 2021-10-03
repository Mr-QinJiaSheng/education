package com.education.admin.api.controller;
import com.education.common.annotation.Param;
import com.education.common.annotation.ParamsType;
import com.education.common.annotation.ParamsValidate;
import com.education.common.base.BaseController;
import com.education.common.constants.Constants;
import com.education.common.model.AdminUserSession;
import com.education.common.model.JwtToken;
import com.education.common.model.OnlineUser;
import com.education.common.model.OnlineUserManager;
import com.education.common.utils.IpUtils;
import com.education.common.utils.Result;
import com.education.common.utils.ResultCode;
import com.education.service.system.SystemAdminService;
import com.education.service.task.LoginSuccessListener;
import com.education.service.task.TaskManager;
import com.education.service.task.TaskParam;
import com.education.service.websocket.SystemWebSocketHandler;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员登录接口
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/26 21:10
 */
@RestController
public class LoginController extends BaseController {

    @Autowired
    private SystemAdminService systemAdminService;
    @Autowired
    private JwtToken jwtToken;
    @Autowired
    private TaskManager taskManager;
    @Autowired
    private OnlineUserManager onlineUserManager;

    @Autowired
    private SystemWebSocketHandler systemWebSocketHandler;
    /**
     * 管理员登录
     * @param request
     * @param params
     * @return
     */
    @PostMapping("login")
    @ParamsValidate(params = {
        @Param(name = "userName", message = "请输入用户名"),
        @Param(name = "password", message = "请输入密码"),
        @Param(name = "key", message = "请传递一个验证码时间戳"),
        @Param(name = "imageCode", message = "请输入验证码")
    }, paramsType = ParamsType.JSON_DATA)
    public Result login(HttpServletRequest request, @RequestBody Map params) {
        String userName = (String)params.get("userName");
        String password = (String)params.get("password");
        String imageCode = (String)params.get("imageCode");
        String key = String.valueOf(params.get("key"));
        String cacheCode = redisCacheBean.get(key);
        if (!imageCode.equalsIgnoreCase(cacheCode)) {
            return Result.fail(ResultCode.FAIL, "验证码错误");
        }
        Result result = systemAdminService.login(userName, password);
        if (result.isSuccess()) {

            // 加载用户菜单及其权限标识
            AdminUserSession adminUserSession = systemAdminService.getAdminUserSession();
            Integer userId = (Integer) adminUserSession.getUserMap().get("id");
            systemAdminService.checkOnlineUser(userId); // 校验用户是否已登录
            // 将目前的登录得用户添加到用户容器
            String sessionId = request.getSession().getId();
            OnlineUser onlineUser = new OnlineUser(sessionId, adminUserSession);
            onlineUserManager.addOnlineUser(userId, onlineUser);// 将显著登录的用户添加的用户容器
            systemAdminService.loadUserMenuAndPermission(adminUserSession);
            params.clear();
            // 创建一个token
            String token = jwtToken.createToken(adminUserSession.getUserMap().get("id"), Constants.FIVE_DAY_TIME_OUT);
            params.put("token", token);
            Map userInfo = new HashMap<>();
            userInfo.put("id", adminUserSession.getUserMap().get("id"));
            userInfo.put("menuList", adminUserSession.getMenuList());
            userInfo.put("permissionList", adminUserSession.getPermissionList());
            userInfo.put("login_name", adminUserSession.getUserMap().get("login_name"));
            params.put("userInfo", userInfo);

            // 更新用户登录信息
            TaskParam taskParam = new TaskParam(LoginSuccessListener.class);
            // 获取之前用户的登录次数
            Integer loginCount = (Integer) adminUserSession.getUserMap().get("login_count");
            taskParam.put("login_count", ++loginCount);
            taskParam.put("login_ip", IpUtils.getAddressIp(request));
            taskParam.put("last_login_time", new Date());
            taskParam.put("id", adminUserSession.getUserMap().get("id"));
            taskManager.pushTask(taskParam);
            result.setData(params);
        }
        return result;
    }

    /**
     * 用户退出
     * @return
     */
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        systemWebSocketHandler.clearWebSocketSession(sessionId);

        Integer userId = (Integer) systemAdminService.getAdminUser().get("id");
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        // 清除在线用户
        onlineUserManager.removeOnlineUser(userId);
        return Result.success(ResultCode.SUCCESS, "退出成功");
    }

    @GetMapping("unAuth")
    public Result unAuth() {
        return Result.fail(ResultCode.NOT_AUTH, "用户未认证");
    }
}
