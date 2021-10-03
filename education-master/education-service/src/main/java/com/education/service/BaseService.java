package com.education.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.education.common.model.AdminUserSession;
import com.education.common.utils.ObjectUtils;
import com.education.common.utils.Result;
import com.education.common.utils.ResultCode;
import com.education.common.utils.SpringBeanManager;
import com.education.mapper.BaseMapper;
import com.education.service.task.TaskManager;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.session.RowBounds;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * service 父类
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/26 21:15
 */
public abstract class BaseService<M extends BaseMapper> {

    private static final String DEFAULT_PAGE_METHOD = "queryList";
    @Autowired
    protected M mapper;
    @Autowired
    protected TaskManager taskManager;

    protected static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    public Result pagination(Map params, Class<? extends BaseMapper> mapperClass, String methodName) {
       try {
           Integer offset = RowBounds.NO_ROW_OFFSET;
           Integer limit = RowBounds.NO_ROW_LIMIT;
           if (ObjectUtils.isNotEmpty(params.get("offset"))) {
               offset = Integer.parseInt((String) params.get("offset"));
           }

           if (ObjectUtils.isNotEmpty(params.get("limit"))) {
               limit = Integer.parseInt((String) params.get("limit"));
           }

           Page page = PageHelper.offsetPage(offset, limit);

           Object pageResult = null;
           if (ObjectUtils.isNotEmpty(mapperClass)) {
              Method method = mapperClass.getMethod(methodName, Map.class);
              BaseMapper mapperBean = SpringBeanManager.getBean(mapperClass);
              method.invoke(mapperBean, params);
           } else {
               pageResult = mapper.queryList(params);
           }
           Map resultMap = new HashMap<>();
           resultMap.put("total", page.getTotal()); // 总条数
           resultMap.put("dataList", pageResult);
           return Result.success(resultMap);
       } catch (Exception e) {
           logger.error("分页异常", e);
       }
       return Result.fail();
    }

    /**
     * 更新shiro 缓存中的用户信息，避免由于redis 缓存导致获取用户信息不一致问题
     * @param adminUserSession
     */
    public void updateShiroCacheUserInfo(AdminUserSession adminUserSession) {
        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection principals = subject.getPrincipals();
        //realName认证信息的key，对应的value就是认证的user对象
        String realName = principals.getRealmNames().iterator().next();
        //创建一个PrincipalCollection对象
        PrincipalCollection newPrincipalCollection = new SimplePrincipalCollection(adminUserSession, realName);
        // 调用subject的runAs方法，把新的PrincipalCollection放到session里面
        subject.runAs(newPrincipalCollection);
    }

    public Result pagination(Map params) {
       return pagination(params, null, DEFAULT_PAGE_METHOD);
    }

    /**
     * 添加或修改
     * @param updateFlag
     * @param modelBeanMap
     * @return
     */
    public Result saveOrUpdate(boolean updateFlag, Map modelBeanMap) {
        try {
            String message = "";
            if (updateFlag) {
                this.checkParams(modelBeanMap);
                this.update(modelBeanMap);
                message = "修改";
            } else {
                modelBeanMap.put("create_date", new Date());
                this.save(modelBeanMap);
                message = "添加";
            }
            return Result.success(ResultCode.SUCCESS, message + "成功");
        } catch (Exception e) {
            logger.error("操作异常", e);
        }
        return Result.success(ResultCode.FAIL, "操作异常");
    }


    /**
     * 添加或修改数据
     * @param params
     * @return
     */
    public Result saveOrUpdate(Map params) {
        Integer id = (Integer) params.get("id");
        boolean updateFlag = false;
        if (ObjectUtils.isNotEmpty(id)) {
            updateFlag = true;
        }
        return this.saveOrUpdate(updateFlag, params);
    }

    /**
     * 移除时间字段
     * @param params
     */
    protected void checkParams(Map params) {
        if (params != null) {
            params.remove("create_date");
            params.remove("last_login_time");
        }
    }

    /**
     * 添加数据
     * @param modelMap
     * @return
     */
    public int save(Map modelMap) {
        Map params = new HashMap();
        params.put("params", modelMap);
        Integer result = mapper.save(params);
        Object id = params.get("id");
        // 如果主键id不为空的话，直接返回主键id
        if (ObjectUtils.isNotEmpty(id)) {
            return TypeUtils.castToInt(id);
        }
        return result;
    }

    /**
     * 更新数据
     * @param modelMap
     * @return
     */
    public int update(Map modelMap) {
        return mapper.update(modelMap);
    }

    public Map getAdminUser() {
        AdminUserSession adminSession = getAdminUserSession();
        if (ObjectUtils.isNotEmpty(adminSession)) {
            return adminSession.getUserMap();
        }
        return null;
    }

    public Integer getAdminUserId() {
        if (ObjectUtils.isNotEmpty(getAdminUser())) {
            return (Integer) getAdminUser().get("id");
        }
        return null;
    }

    public Result deleteById(Map modelBeanMap) {
        return deleteById((Integer) modelBeanMap.get("id"));
    }

    public Result deleteById(Integer id) {
        int result = mapper.deleteById(id);
        if (result > 0) {
            return Result.success(ResultCode.SUCCESS, "删除成功");
        }
        return Result.fail(ResultCode.FAIL, "删除数据异常");
    }

    /**
     * 获取管理员用户信息
     * @return
     */
    public AdminUserSession getAdminUserSession() {
        Subject subject = SecurityUtils.getSubject();
        AdminUserSession userSession = (AdminUserSession)subject.getPrincipal();
        if (ObjectUtils.isNotEmpty(userSession)) {
            return userSession;
        }
        return null;
    }
}
