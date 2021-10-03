package com.education.mapper.system;

import com.education.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/30 20:50
 */
public interface SystemRoleMenuMapper extends BaseMapper {

    List<Map> getMenuListByRoleIds(Map params);

    int deleteRoleMenuByRoleId(Integer roleId);

    /**
     * 删除角色关联的菜单
     * @param roleId
     * @return
     */
    int deleteByRoleId(Integer roleId);

    /**
     * 批量删除角色关联的菜单
     * @param roleIds
     * @return
     */
    int deleteByRoleIds(List<Integer> roleIds);
}
