package com.education.service.system;

import com.education.common.utils.MapTreeUtils;
import com.education.common.utils.ObjectUtils;
import com.education.common.utils.Result;
import com.education.common.utils.ResultCode;
import com.education.mapper.system.SystemMenuMapper;
import com.education.service.BaseService;
import org.apache.shiro.crypto.hash.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单管理业务层
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/30 20:36
 */
@Service
public class SystemMenuService extends BaseService<SystemMenuMapper> {

    @Autowired
    private SystemRoleMenuService systemRoleMenuService;

    public List<Map> queryList(Map params) {
        return mapper.queryList(params);
    }

    public Result findById(Integer menuId) {
       Map menuMap = mapper.findById(menuId);
       List<Map> menuList = mapper.queryList(new HashMap());
       List<Map> parentMenuList = MapTreeUtils.getParentList(menuList, (Integer) menuMap.get("parent_id"));
       List<Integer> parentIds = parentMenuList.stream()
               .map(item -> (Integer)item.get("id"))
               .collect(Collectors.toList());
       menuMap.put("parentIds", parentIds);
       return Result.success(menuMap);
    }

    public List<Integer> getRolePermission(Integer roleId) {
        // 获取角色拥有的权限列表
        List<Integer> roleIds = new ArrayList<>();
        roleIds.add(roleId);
        Map params = new HashMap<>();
        params.put("roleIds", roleIds);
        List<Map> roleMenuList = systemRoleMenuService.getMenuListByRoleIds(params);
        List<Integer> menuIds = new ArrayList<>();
        // 找出最后一级的子菜单集合
        if (ObjectUtils.isNotEmpty(roleMenuList)) {
            roleMenuList.forEach(roleMenu -> {
                Integer menuId = (Integer) roleMenu.get("id");
                Integer parentId = (Integer) roleMenu.get("parent_id");
                if (parentId != ResultCode.FAIL) {
                    List<Map> childrenList = MapTreeUtils.getChildrenTree(roleMenuList, menuId);
                    if (ObjectUtils.isEmpty(childrenList)) {
                         menuIds.add(menuId);
                    }
                }
            });
        }
        return menuIds;
    }
}
