package com.education.admin.api.controller;

import com.education.common.base.BaseController;
import com.education.common.utils.Result;
import com.education.service.system.SystemDictService;
import com.education.service.system.SystemDictValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * 字典管理接口
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/7/19 11:46
 */
@RestController
public class DictController extends BaseController {

    @Autowired
    private SystemDictService systemDictService;
    @Autowired
    private SystemDictValueService systemDictValueService;

    /**
     * 获取字典类型列表
     * @param params
     * type 字典类型
     * @return
     */
    @GetMapping("/dict/list")
    public Result getDictList(@RequestParam Map params) {
        return systemDictService.pagination(params);
    }

    /**
     * 根据字典类型获取字典值
     * @param params
     * type 字典类型
     * parentId 字典值parentId
     * @return
     */
    @GetMapping("/dict/getDictValueByType")
    public Result getDictValueByType(@RequestParam Map params) {
        return Result.success(systemDictValueService.getDictValueByType(params));
    }
}
