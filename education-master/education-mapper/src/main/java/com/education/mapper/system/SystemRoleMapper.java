package com.education.mapper.system;

import com.education.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/25 15:18
 */
public interface SystemRoleMapper extends BaseMapper {

    Map findByRoleName(String name);

    /**
     * 批量删除角色
     * @param roleIds
     * @return
     */
    int deleteByRoleIds(List<Integer> roleIds);
}
