package com.education.admin.api.controller.system;

import com.education.common.annotation.Param;
import com.education.common.annotation.ParamsType;
import com.education.common.annotation.ParamsValidate;
import com.education.common.base.BaseController;
import com.education.common.utils.Result;
import com.education.service.system.SystemAdminService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员管理api接口
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/5/24 12:50
 */
@RestController
@RequestMapping("/system/admin")
public class SystemAdminController extends BaseController {

    @Autowired
    private SystemAdminService systemAdminService;

    /**
     * 管理员列表接口
     * @param params
     * @return
     */
    @GetMapping
  //  @RequiresPermissions("system:admin:list")
    public Result list(@RequestParam Map params) {
        return systemAdminService.pagination(params);
    }

    /**
     * 获取管理员角色id集合
     * @param adminId
     * @return
     */
    @GetMapping("getRoleIdsByAdminId")
    public Result getRoleIdsByAdminId(Integer adminId) {
        return systemAdminService.getRoleIdsByAdminId(adminId);
    }

    /**
     * 管理员重置密码
     * @param params
     * @return
     */
    @PostMapping("resettingPassword")
    @ParamsValidate(
         params = {
                 @Param(name = "password", message = "请输入新密码"),
                 @Param(name = "confirmPassword", message = "请输入确认密码"),
         }, paramsType = ParamsType.JSON_DATA
    )
    @RequiresPermissions("system:admin:resettingPassword")
    public Result resettingPassword(@RequestBody Map params) {
        return systemAdminService.resettingPassword(params);
    }



    /**
     * 添加或修改管理员
     * @param adminMap
     * @return
     */
    @PostMapping
    @ParamsValidate(params = {
       @Param(name = "login_name", message = "请输入账户名称"),
       @Param(name = "roleIds", message = "请选择角色")
    }, paramsType = ParamsType.JSON_DATA)
    @RequiresPermissions(value = {"system:admin:save", "system:admin:update"}, logical = Logical.OR)
    public Result saveOrUpdate(@RequestBody Map adminMap) {
        return systemAdminService.saveOrUpdate(adminMap);
    }


    @DeleteMapping("/deleteById")
    @RequiresPermissions("system:admin:deleteById")
    public Result deleteById(@RequestBody Map adminMap) {
        return systemAdminService.deleteById(adminMap);
    }

}
