package com.education.common.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/27 20:46
 */
public class AdminUserSession implements Serializable {

    private Map userMap;
    private Set<Integer> roleIds; // 用户角色集合
    private List<Map> menuList; // 用户菜单集合


    public boolean isSuperAdmin() {
        return (boolean) userMap.get("super_flag");
    }


    public List<Map> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Map> menuList) {
        this.menuList = menuList;
    }

    public Set<String> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(Set<String> permissionList) {
        this.permissionList = permissionList;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Map getUserMap() {
        return userMap;
    }

    public void setUserMap(Map userMap) {
        this.userMap = userMap;
    }

    public Set<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Integer> roleIds) {
        this.roleIds = roleIds;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    private Set<String> permissionList = new HashSet<>(); // 用户权限标识
    private String sessionId;

    public AdminUserSession(Map userMap) {
        this.userMap = userMap;
    }

    /**
     * 添加权限标识
     * @param permission
     */
    public void addPermission(String permission) {
        permissionList.add(permission);
    }
}
