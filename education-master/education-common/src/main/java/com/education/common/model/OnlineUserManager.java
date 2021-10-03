package com.education.common.model;

import com.education.common.cache.CacheBean;
import com.education.common.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 在线用户管理器
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/7/7 20:07
 */
public class OnlineUserManager {

    private CacheBean redisCacheBean;

    private static final String USER_ID_CACHE = "user:id:cache";

    public OnlineUserManager(CacheBean cacheBean) {
        this.redisCacheBean = cacheBean;
    }

    /**
     * 添加在线用户
     * @param userId
     * @param onlineUser
     */
    public void addOnlineUser(Integer userId, OnlineUser onlineUser) {
        redisCacheBean.put(USER_ID_CACHE, userId, onlineUser);
    }

    /**
     * 删除在线用户
     * @param userId
     */
    public void removeOnlineUser(Integer userId) {
        redisCacheBean.remove(USER_ID_CACHE, userId);
    }

    /**
     * 根据userId获取在线用户
     * @param userId
     * @return
     */
    public OnlineUser getOnlineUser(Integer userId) {
        return redisCacheBean.get(USER_ID_CACHE, userId);
    }

    public List<OnlineUser> getAllOnlineUser() {
        List<OnlineUser> onlineUserList = new ArrayList<>();
        Set<Integer> userIds = (Set<Integer>) redisCacheBean.getKeys(USER_ID_CACHE);
        if (ObjectUtils.isNotEmpty(userIds)) {
            userIds.forEach(userId -> {
                onlineUserList.add(getOnlineUser(userId));
            });
        }
        return onlineUserList;
    }


    /**
     * 删除所有在线用户
     */
    public void clear() {
        Set<Integer> userIds = (Set<Integer>) redisCacheBean.getKeys(USER_ID_CACHE);
        if (ObjectUtils.isNotEmpty(userIds)) {
            userIds.forEach(userId -> {
               redisCacheBean.remove(userId);
            });
        }
    }
}
