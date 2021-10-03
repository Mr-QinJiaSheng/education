package com.education.admin.api.controller.system;

import com.education.common.base.BaseController;
import com.education.common.utils.Result;
import com.education.service.system.SystemRoleService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 角色管理api接口
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/5/10 14:39
 */
@RestController
@RequestMapping("/system/role")
public class SystemRoleController extends BaseController {

    @Autowired
    private SystemRoleService systemRoleService;

    /**
     * 获取角色列表
     * @param params
     * @return
     */
    @GetMapping
    @RequiresPermissions("system:role:list")
    public Result list(@RequestParam Map params) {
        return systemRoleService.pagination(params);
    }

    /**
     * 添加或修改角色
     * @param roleMap
     * @return
     */
    @PostMapping
    @RequiresPermissions(value = {"system:role:save", "system:role:update"}, logical = Logical.OR)
    public Result saveOrUpdate(@RequestBody Map roleMap) {
        return systemRoleService.saveOrUpdate(roleMap);
    }

    /**
     * 根据id角色
     * @param roleMap
     * @return
     */
    @DeleteMapping
    @RequiresPermissions("system:role:deleteById")
    public Result deleteById(@RequestBody Map roleMap) {
        return systemRoleService.deleteById((Integer) roleMap.get("id"));
    }

    /**
     * 批量删除角色
     * @param roleIds
     * @return
     */
    @DeleteMapping("batchDeleteByRoleIds")
    @RequiresPermissions("system:role:deleteById")
    public Result batchDeleteByRoleIds(@RequestBody List<Integer> roleIds) {
        return systemRoleService.batchDeleteByRoleIds(roleIds);
    }

    /**
     * 保存角色权限
     * 角色id
     * 角色权限集合
     * @param params
     * @return
     */
    @PostMapping("savePermission")
    @RequiresPermissions("system:role:savePermission")
    public Result saveRolePermission(@RequestBody Map params) {
        return systemRoleService.saveRolePermission(params);
    }

}
