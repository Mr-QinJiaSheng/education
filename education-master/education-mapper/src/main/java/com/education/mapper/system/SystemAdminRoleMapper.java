package com.education.mapper.system;

import com.education.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/30 20:39
 */
public interface SystemAdminRoleMapper extends BaseMapper {

    List<Map> findRoleListByAdminId(Integer adminId);

    int deleteByRoleId(Integer roleId);

    /**
     * 批量删除角色关联的菜单
     * @param roleIds
     * @return
     */
    int deleteByRoleIds(List<Integer> roleIds);

    /**
     * 根据管理员id删除信息
     * @param adminId
     * @return
     */
    int deleteByAdminId(Integer adminId);
}
