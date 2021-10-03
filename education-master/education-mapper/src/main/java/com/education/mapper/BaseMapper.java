package com.education.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/21 11:22
 */
public interface BaseMapper {


    Map findById(Integer id);

    /**
     * 添加单条数据
     * @param params
     * @return
     */
    int save(Map params);

    /**
     * 修改数据
     * @param params
     * @return
     */
    int update(@Param("params") Map params);

    /**
     * 批量添加
     * @param params
     * @return
     */
    int batchSave(Map params);

    /**
     * 批量更新
     * @param params
     * @return
     */
    int batchUpdate(Map params);

    /**
     * 根据id 删除数据
     * @param id
     * @return
     */
    int deleteById(Integer id);

    /**
     * 根据id 集合批量删除数据
     * @param ids
     * @return
     */
    int batchDeleteByIds(List<Integer> ids);

    /**
     * 查询列表
     * @param params
     * @return
     */
    List<Map> queryList(Map params);
}
