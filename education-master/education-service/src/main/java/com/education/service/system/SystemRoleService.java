package com.education.service.system;

import com.education.common.exception.BusinessException;
import com.education.common.utils.ObjectUtils;
import com.education.common.utils.Result;
import com.education.common.utils.ResultCode;
import com.education.mapper.system.SystemRoleMapper;
import com.education.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理业务层
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/5/10 14:41
 */
@Service
public class SystemRoleService extends BaseService<SystemRoleMapper> {

    @Autowired
    private SystemRoleMenuService systemRoleMenuService;
    @Autowired
    private SystemAdminRoleService systemAdminRoleService;


    @Override
    public Result saveOrUpdate(boolean updateFlag, Map modelBeanMap) {
        synchronized (this) {
            // 检查角色是否存在
            Map roleMap = mapper.findByRoleName((String) modelBeanMap.get("name"));
            if (ObjectUtils.isNotEmpty(roleMap)) {
                return Result.fail(ResultCode.FAIL, "角色已存在,请勿重复添加");
            }
        }
        return super.saveOrUpdate(updateFlag, modelBeanMap);
    }

    public Result list(Map params) {
        return Result.success(mapper.queryList(params));
    }

    @Override
    @Transactional
    public Result deleteById(Integer id) {
        try {
            // 删除角色
            super.deleteById(id);
            // 删除角色关联的菜单
            systemRoleMenuService.deleteByRoleId(id);
            // 删除角色用户关联信息
            systemAdminRoleService.deleteByRoleId(id);
            return Result.success(ResultCode.SUCCESS, "删除角色成功");
        } catch (Exception e) {
            logger.error("删除角色异常", e);
            throw new BusinessException(new ResultCode(ResultCode.FAIL, "删除角色异常"));
        }
    }

    @Transactional
    public Result batchDeleteByRoleIds(List<Integer> roleIds) {
        try {
            systemAdminRoleService.deleteByRoleIds(roleIds);
            systemRoleMenuService.deleteByRoleIds(roleIds);
            mapper.deleteByRoleIds(roleIds);
            return Result.success(ResultCode.SUCCESS, "批量删除角色成功");
        } catch (Exception e) {
            logger.error("批量删除角色异常", e);
            throw new BusinessException(new ResultCode(ResultCode.FAIL, "批量删除角色异常"));
        }
    }

    /**
     * 保存角色权限
     * @param params
     * @return
     */
    @Transactional
    public Result saveRolePermission(Map params) {

        try {
            Integer roleId = (Integer) params.get("roleId");
            //删除角色原有的权限
            systemRoleMenuService.deleteRoleMenuByRoleId(roleId);
            //获取权限集合
            List<Integer> permission = (List<Integer>) params.get("permission");
            if (ObjectUtils.isNotEmpty(permission)) {
                List<Map> list = new ArrayList<>();

                permission.forEach(item -> {
                    Map roleMenuMap = new HashMap<>();
                    roleMenuMap.put("menu_id", item);
                    roleMenuMap.put("role_id", roleId);
                    list.add(roleMenuMap);
                });
                systemRoleMenuService.batchSaveRoleMenu(list);
            }
            return Result.success(ResultCode.SUCCESS, "保存角色权限成功");
        } catch (Exception e) {
            logger.error("保存角色权限异常", e);
            throw new BusinessException(new ResultCode(ResultCode.FAIL, "保存角色权限异常"));
        }

    }
}
