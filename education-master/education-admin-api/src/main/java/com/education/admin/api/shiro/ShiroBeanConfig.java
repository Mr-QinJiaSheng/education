package com.education.admin.api.shiro;

import com.education.common.cache.CacheBean;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro bean 实例配置
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/22 11:00
 */
//@Configuration
//@AutoConfigureAfter(ShiroLifecycleBeanPostProcessorConfig.class)
public class ShiroBeanConfig {

    private static final long INVALID_TIME = 3600 * 6 * 1000;

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setUnauthorizedUrl("/unAuth");
        shiroFilterFactoryBean.setLoginUrl("/unAuth");
        Map filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/system/**", "authc");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/image", "anon");
        filterChainDefinitionMap.put("/uploads/**", "anon");
        filterChainDefinitionMap.put("/logout", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        shiroFilterFactoryBean.setFilters(shiroFilterFactoryBean.getFilters());
        return shiroFilterFactoryBean;
    }

    @Bean
    public SessionManager sessionManager(SessionDAO distributeShiroSession) {
        DefaultSessionManager sessionManager = new DefaultWebSessionManager();
        // 设置session
        sessionManager.setGlobalSessionTimeout(INVALID_TIME);
        sessionManager.setSessionDAO(distributeShiroSession);
        return sessionManager;
    }

    @Bean
    public SecurityManager securityManager(SessionManager sessionManager, Realm systemRealm,
                                           CacheManager redisCacheManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(systemRealm);
        securityManager.setCacheManager(redisCacheManager);
        securityManager.setSessionManager(sessionManager);
        return securityManager;
    }

    @Bean
    public SessionDAO distributeShiroSession(CacheBean redisCacheBean) {
        return new DistributeShiroSession(redisCacheBean);
    }

    @Bean
    public RedisCacheManager redisCacheManager(CacheBean redisCacheBean) {
        return new RedisCacheManager(redisCacheBean);
    }



    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),
     * 需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证 *
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能 * @return
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
