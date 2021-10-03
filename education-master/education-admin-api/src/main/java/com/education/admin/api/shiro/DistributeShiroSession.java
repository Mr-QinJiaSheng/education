package com.education.admin.api.shiro;

import com.education.common.cache.CacheBean;
import com.education.common.utils.ObjectUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/7/3 20:12
 */
public class DistributeShiroSession extends AbstractSessionDAO {

    private CacheBean redisCacheBean;

    private static final String SESSION_KEY = "user.session.cache";

    private static final int ONE_DAY = 24 * 60 * 60; //默认失效时间为1天

    private long expire = ONE_DAY;

    /**
     * 设置session失效时间
     * @param expire
     */
    public void setExpire(long expire) {
        this.expire = expire;
    }

    public DistributeShiroSession (CacheBean redisCacheBean) {
        this.redisCacheBean = redisCacheBean;
    }

    // 创建sessionId
    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        // 将session信息保存到redis
        this.saveSessionToCache(sessionId, session);
        return sessionId;
    }

    private void saveSessionToCache(Serializable sessionId, Session session) {
        if (ObjectUtils.isEmpty(sessionId)) {
            throw new NullPointerException("id argument cannot be null.");
        }
        // 设置session时间为24小时
        redisCacheBean.put(SESSION_KEY, sessionId, session, ONE_DAY, TimeUnit.SECONDS);
    }

    /**
     * 用来获取session实例
     * @param sessionId
     * @return
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        return redisCacheBean.get(SESSION_KEY, sessionId);
    }

    /**
     * 更新session实例信息
     * @param session
     * @throws UnknownSessionException
     */
    @Override
    public void update(Session session) throws UnknownSessionException {
        this.saveSessionToCache(session.getId(), session);
    }

    /**
     * 用来删除session信息
     * @param session
     */
    @Override
    public void delete(Session session) {
        redisCacheBean.remove(SESSION_KEY, session.getId());
    }

    /**
     * 用来获取所有session集合
     * @return
     */
    @Override
    public Collection<Session> getActiveSessions() {
        Collection<String> keys = redisCacheBean.getKeys(SESSION_KEY);
        List<Session> sessionList = new ArrayList();
        if (ObjectUtils.isNotEmpty(keys)) {
            keys.forEach(key -> {
               Session session = redisCacheBean.get(SESSION_KEY, key);
               sessionList.add(session);
            });
            return Collections.unmodifiableCollection(sessionList);
        }
        return Collections.emptySet();
    }
}
