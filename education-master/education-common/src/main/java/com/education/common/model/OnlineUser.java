package com.education.common.model;

import java.io.Serializable;

/**
 * 用来存储在线用户信息
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/7/7 20:05
 */
public class OnlineUser implements Serializable {

    private String sessionId; // 客户端会话id
    private AdminUserSession adminUserSession;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public AdminUserSession getAdminUserSession() {
        return adminUserSession;
    }

    public void setAdminUserSession(AdminUserSession adminUserSession) {
        this.adminUserSession = adminUserSession;
    }

    public OnlineUser(String sessionId, AdminUserSession adminUserSession) {
        this.sessionId = sessionId;
        this.adminUserSession = adminUserSession;
    }
}
