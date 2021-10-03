package com.education.admin.api.controller.system;

import com.education.common.base.BaseController;
import com.education.common.utils.MapTreeUtils;
import com.education.common.utils.Result;
import com.education.service.system.SystemMenuService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单管理api接口
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/5/17 15:25
 */
@RestController
@RequestMapping("/system/menu")
public class SystemMenuController extends BaseController {


    @Autowired
    private SystemMenuService systemMenuService;

    /**
     * 菜单列表
     * @return
     */
    @GetMapping("getMenuList")
    @RequiresPermissions("system:menu:list")
    public Result getMenuList() {
        Result result = systemMenuService.pagination(new HashMap());
        Map dataMap = result.getData();
        List<Map> dataList = MapTreeUtils.buildTreeData((List<Map>)dataMap.get("dataList"));
        dataMap.put("dataList", dataList);
        return result;
    }

    /**
     * 添加或修改菜单
     * @param params
     * @return
     */
    @PostMapping
    @RequiresPermissions(value = {"system:menu:save", "system:menu:update"}, logical = Logical.OR)
    public Result saveOrUpdate(@RequestBody Map params) {
        return systemMenuService.saveOrUpdate(params);
    }

    /**
     * 根据id删除菜单
     * @param params
     * @return
     */
    @DeleteMapping
    @RequiresPermissions("system:menu:deleteById")
    public Result deleteById(@RequestBody Map params) {
        return systemMenuService.deleteById(params);
    }

    /**
     * 获取菜单详情
     * @param menuId
     * @return
     */
    @GetMapping("findById")
    public Result findById(Integer menuId) {
        return systemMenuService.findById(menuId);
    }

    /**
     * 获取角色权限列表
     * @param roleId
     * @return
     */
    @GetMapping("getRolePermission")
    public Result getRolePermission(Integer roleId) {
        return Result.success(systemMenuService.getRolePermission(roleId));
    }
}
