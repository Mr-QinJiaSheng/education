package com.education.service.system;

import com.alibaba.fastjson.JSONObject;
import com.education.common.exception.BusinessException;
import com.education.common.model.AdminUserSession;
import com.education.common.model.OnlineUser;
import com.education.common.model.OnlineUserManager;
import com.education.common.utils.*;
import com.education.mapper.system.SystemAdminMapper;
import com.education.service.BaseService;
import com.education.service.websocket.SystemWebSocketHandler;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 管理员service
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/26 21:15
 */
@Service
public class SystemAdminService extends BaseService<SystemAdminMapper> {

    private static final Logger logger = LoggerFactory.getLogger(SystemAdminService.class);
    @Autowired
    private SystemMenuService systemMenuService;
    @Autowired
    private SystemAdminRoleService systemAdminRoleService;
    @Autowired
    private SystemRoleMenuService systemRoleMenuService;
    @Resource
    private OnlineUserManager onlineUserManager;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private SystemWebSocketHandler systemWebSocketHandler;
    /**
     * 第一 去用户容器中查找这个用户是否在设备上已经登录
     * 如果已经登录的话， 使用websocket 异步发送一条消息推送
     * @param userId
     */
    public void checkOnlineUser(Integer userId) {
        OnlineUser onlineUser = onlineUserManager.getOnlineUser(userId);
        if (ObjectUtils.isNotEmpty(onlineUser)) { //代表已经在其他设备登录
            // 需要使用webSocket发送一条消息推送
            String sessionId = onlineUser.getSessionId(); // 获取已经登录的设备会话id

            onlineUserManager.removeOnlineUser(userId); // 踢出原来的用户信息
            String ip = IpUtils.getAddressIp(request);
            taskManager.pushTask(() -> {
                try {
                    // Thread.sleep(10000);
                    Thread.sleep(1000);
                    String address = IpUtils.getAddressByIp(ip);
                    address = address == null ? "在其它设备上" : address;
                    ResultCode resultCode = new ResultCode(ResultCode.SUCCESS, "您的账号已在" + address + "登录，5秒后将自动下线，如非本人操作，请立即修改密码");
                    String message = JSONObject.toJSONString(resultCode);
                    systemWebSocketHandler.sendMessage(sessionId, message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        }
    }



    /**
     * 管理员登录
     * @param userName
     * @param password
     * @return
     */
    public Result login(String userName, String password) {
        Result result = new Result(ResultCode.SUCCESS, "登录成功");
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(userName, password);
        try {
            subject.login(usernamePasswordToken); // 执行用户登录
        } catch (Exception e) {
            result.setCode(ResultCode.FAIL);
            if (e instanceof UnknownAccountException) {
                result.setMessage("用户不存在");
            } else {
                result.setMessage("用户名或密码错误");
            }

            Throwable throwable = e.getCause();
            if (ObjectUtils.isNotEmpty(throwable)) {
                if (throwable instanceof BusinessException) {
                    result.setMessage(throwable.getMessage());
                }
            }
            logger.error("登录失败", e);
        }
        return result;
    }


    @Override
    @Transactional
    public Result deleteById(Map modelBeanMap) {
        boolean superFlag = (boolean) modelBeanMap.get("super_flag");
        if (superFlag) {
            return Result.fail(ResultCode.FAIL, "您不能删除系统超级管理员");
        }
        // 根据管理员id删除管理员
        Integer adminId = (Integer) modelBeanMap.get("id");
        super.deleteById(adminId);
        // 删除管理员与角色的关联关系
        systemAdminRoleService.deleteByAdminId(adminId);
        return Result.success(ResultCode.SUCCESS, "删除成功");
    }

    /**
     * 获取用户菜单及其权限
     * @param adminUserSession
     */
    public void loadUserMenuAndPermission(AdminUserSession adminUserSession) {
        List<Map> menuList = null;
        // 如果是超级管理员，加载所有菜单及其权限
        if (adminUserSession.isSuperAdmin()) {
            menuList = systemMenuService.queryList(null);
        } else {
            Integer adminId = (Integer) adminUserSession.getUserMap().get("id");
            // 先查询用户角色
            List<Map> roleList = systemAdminRoleService.findRoleListByAdminId(adminId);
            if (ObjectUtils.isNotEmpty(roleList)) {
                Set<Integer> roleIds = new HashSet<>();
                roleList.forEach(roleInfo -> {
                    roleIds.add((Integer) roleInfo.get("role_id"));
                });
                adminUserSession.setRoleIds(roleIds);
                Map params = new HashMap<>();
                params.put("roleIds", roleIds);
                menuList = systemRoleMenuService.getMenuListByRoleIds(params);
            }
        }

        menuList.forEach(menu -> {
            String permission = (String) menu.get("permission"); // 用户权限标识
            if (ObjectUtils.isNotEmpty(permission)) {
                adminUserSession.addPermission(permission);
            }
        });

        List<Map> treeMenuList = MapTreeUtils.buildTreeData(menuList);
        adminUserSession.setMenuList(treeMenuList);
        this.updateShiroCacheUserInfo(adminUserSession);
    }

    @Override
    @Transactional
    public Result saveOrUpdate(Map params) {
        String message = "";
        try {
            String mobile = (String) params.get("mobile");
            if (ObjectUtils.isNotEmpty(mobile)) {
                boolean mobileFlag = RegexUtils.compile(RegexUtils.MOBILE_REGEX, mobile);
                if (!mobileFlag) {
                    return Result.fail(ResultCode.FAIL, "非法手机号");
                }
            }

            List<Integer> roleIds = (List<Integer>) params.get("roleIds");
            params.remove("roleIds"); // 移除这个字段

            Date now = new Date();
            Integer adminId = null;
            if (ObjectUtils.isNotEmpty(params.get("id"))) { // 修改管理员
                params.put("update_date", now);
                adminId = (Integer) params.get("id");
                // 先删除管理员与角色关联信息
                systemAdminRoleService.deleteByAdminId(adminId);
                super.update(params);
                message = "修改管理员";
            } else {
                String password = (String) params.get("password");
                if (ObjectUtils.isEmpty(password)) {
                    return Result.fail(ResultCode.FAIL, "请输入密码");
                }
                String confirmPassword = (String) params.get("confirmPassword");
                if (ObjectUtils.isEmpty(confirmPassword)) {
                    return Result.fail(ResultCode.FAIL, "请输入确认密码");
                }
                if (!password.equals(confirmPassword)) {
                    return Result.fail(ResultCode.FAIL, "密码与确认密码不一致");
                }

                params.remove("confirmPassword");
                // 保存管理员信息
                String encrypt = EncryptUtil.encodeSalt(EncryptUtil.generatorKey()); // 生成密码hash
                password = EncryptUtil.getMd5(password, encrypt); // 生成加密之后的密码
                params.put("password", password);
                params.put("encrypt", encrypt);
                params.put("create_date", now);
                message = "添加管理员";
                adminId = this.save(params); // 保存管理员信息
            }
            // 保存管理员与角色关联信息
            List<Map> roleList = new ArrayList<>();
            // 重新保存管理员与角色的关联信息
            for (Integer roleId : roleIds) {
                Map roleMap = new HashMap<>();
                roleMap.put("role_id", roleId);
                roleMap.put("admin_id", adminId);
                roleList.add(roleMap);
            }
            Map dataMap = new HashMap<>();
            dataMap.put("list", roleList);
            systemAdminRoleService.batchSave(dataMap);
            return Result.success(ResultCode.SUCCESS, message + "成功");
        } catch (Exception e) {
            logger.error(message + " 异常", e);
            throw new BusinessException(new ResultCode(ResultCode.FAIL, message + "异常"));
        }
    }

    public int update(Map params) {
        return mapper.update(params);
    }



    public Result getRoleIdsByAdminId(Integer adminId) {
        List<Map> adminRoleList = systemAdminRoleService.findRoleListByAdminId(adminId);
        List<Integer> roleIds = new ArrayList<>();
        // 获取角色id集合
        if (ObjectUtils.isNotEmpty(adminRoleList)) {
            roleIds = adminRoleList.stream()
                    .map(item -> (Integer)item.get("role_id"))
                    .collect(Collectors.toList());
        }
        return Result.success(roleIds);
    }

    public Result resettingPassword(Map params) {
        String password = (String) params.get("password");
        String confirmPassword = (String) params.get("confirmPassword");
        if (!password.equals(confirmPassword)) {
            return Result.fail(ResultCode.FAIL, "密码与确认密码不一致");
        }
        String encrypt = (String) params.get("encrypt");
        password = EncryptUtil.getMd5(password, encrypt);
        Integer id = (Integer) params.get("id");
        params.clear();
        params.put("id", id);
        params.put("password", password);
        this.update(params);
        return Result.success(ResultCode.SUCCESS, "密码重置成功");
    }
}
