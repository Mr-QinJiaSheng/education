package com.education.service.system;

import com.education.mapper.system.SystemAdminRoleMapper;
import com.education.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/30 20:41
 */
@Service
public class SystemAdminRoleService extends BaseService<SystemAdminRoleMapper> {

    public List<Map> findRoleListByAdminId(Integer adminId) {
        return mapper.findRoleListByAdminId(adminId);
    }

    public int deleteByRoleId(Integer roleId) {
        return mapper.deleteByRoleId(roleId);
    }

    public int deleteByRoleIds(List<Integer> roleIds) {
        return mapper.deleteByRoleIds(roleIds);
    }


    public int deleteByAdminId(Integer adminId) {
        return mapper.deleteByAdminId(adminId);
    }

    public int batchSave(Map params) {
        return mapper.batchSave(params);
    }
}
