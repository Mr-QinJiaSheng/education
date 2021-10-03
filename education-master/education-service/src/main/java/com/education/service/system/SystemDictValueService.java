package com.education.service.system;

import com.education.mapper.system.SystemDictValueMapper;
import com.education.service.BaseService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class SystemDictValueService extends BaseService<SystemDictValueMapper> {

    private static final String DICT_VALUE_CACHE_NAME = "system:dict:value:";

    public List<Map> getDictValueByType(Map params) {
        return mapper.getDictValueByType(params);
    }

    public List<Map> getDictValueByParentId(Map params) {
        return mapper.getDictValueByParentId(params);
    }
}
