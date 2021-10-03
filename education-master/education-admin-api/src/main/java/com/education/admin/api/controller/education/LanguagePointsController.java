package com.education.admin.api.controller.education;

import com.education.common.base.BaseController;
import com.education.common.utils.MapTreeUtils;
import com.education.common.utils.Result;
import com.education.service.education.LanguagePointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 知识点管理
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/7/25 14:14
 */
@RequestMapping("/system/languagePoints")
@RestController
public class LanguagePointsController extends BaseController {

    @Autowired
    private LanguagePointsService languagePointsService;

    /**
     * 知识点分页列表查询
     * @param params
     * @return
     */
    @GetMapping
    public Result list(@RequestParam Map params) {
        Result result = languagePointsService.pagination(params);
        // 知识点tree树形结构
        boolean treeDataFlag = Boolean.getBoolean((String) params.get("treeDataFlag"));
        if (treeDataFlag) {
            Map data = result.getData();
            List<Map> dataList = (List<Map>) data.get("dataList");
            dataList = MapTreeUtils.buildTreeData(dataList);
            data.put("dataList", dataList);
            result.setData(data);
        }
        return result;
    }
}
