package com.education.mapper.system;

import com.education.mapper.BaseMapper;

import java.util.Map;


/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/21 11:25
 */
public interface SystemAdminMapper extends BaseMapper {

    /**
     * 根据用户名查找用户
     * @param userName
     * @return
     */
    Map findByLoginName(String userName);
}
