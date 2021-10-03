package com.education.mapper.system;

import com.education.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

public interface SystemDictValueMapper extends BaseMapper {

    List<Map> getDictValueByType(Map params);

    List<Map> getDictValueByParentId(Map params);

    int deleteByDictId(Integer dictId);

}
