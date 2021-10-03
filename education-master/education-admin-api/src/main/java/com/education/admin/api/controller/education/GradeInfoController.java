package com.education.admin.api.controller.education;

import com.education.common.annotation.Param;
import com.education.common.annotation.ParamsType;
import com.education.common.annotation.ParamsValidate;
import com.education.common.base.BaseController;
import com.education.common.utils.Result;
import com.education.service.education.GradeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/7/19 13:38
 */
@RestController
@RequestMapping("/system/grade")
public class GradeInfoController extends BaseController {

    @Autowired
    private GradeInfoService gradeInfoService;

    /**
     * 年级列表接口
     * @param params
     * @return
     */
    @GetMapping
    public Result list(@RequestParam Map params) {
        return gradeInfoService.pagination(params);
    }

    /**
     * 添加或修改课程
     * @param params
     * @return
     */
    @PostMapping
    @ParamsValidate(params = {
       @Param(name = "name", message = "请输入年级名称"),
    }, paramsType = ParamsType.JSON_DATA)
    public Result saveOrUpdate(@RequestBody Map params) {
        params.put("user_id", gradeInfoService.getAdminUserId());
        return gradeInfoService.saveOrUpdate(params);
    }

}
