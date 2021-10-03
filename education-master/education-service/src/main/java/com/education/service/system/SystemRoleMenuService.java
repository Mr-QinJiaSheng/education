package com.education.service.system;

import com.education.mapper.system.SystemRoleMenuMapper;
import com.education.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/30 20:55
 */
@Service
public class SystemRoleMenuService extends BaseService<SystemRoleMenuMapper> {

    public List<Map> getMenuListByRoleIds(Map params) {
        return mapper.getMenuListByRoleIds(params);
    }

    /**
     * 根据角色id删除角色权限
     * @param roleId
     * @return
     */
    public int deleteRoleMenuByRoleId(Integer roleId) {
        return mapper.deleteRoleMenuByRoleId(roleId);
    }

    public int batchSaveRoleMenu(List<Map> roleMenuList) {
        Map params = new HashMap<>();
        params.put("list", roleMenuList);
        return mapper.batchSave(params);
    }


    public int deleteByRoleId(Integer roleId) {
        return mapper.deleteByRoleId(roleId);
    }

    public int deleteByRoleIds(List<Integer> roleIds) {
        return mapper.deleteByRoleIds(roleIds);
    }
}
