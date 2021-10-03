package com.education.mapper.system;

import com.education.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/3/9 21:16
 */
public interface SystemDictMapper extends BaseMapper {

    String GET_DICT_VALUE_LIST = "getDictValueList";

    List<Map> getDictValueList(Map params);

    Map findByCodeAndType(@Param("typeName") String typeName, @Param("code") Integer code);
}
