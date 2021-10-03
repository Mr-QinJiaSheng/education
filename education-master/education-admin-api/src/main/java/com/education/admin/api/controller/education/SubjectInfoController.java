package com.education.admin.api.controller.education;

import com.education.common.base.BaseController;
import com.education.common.utils.Result;
import com.education.service.education.GradeInfoService;
import com.education.service.education.SubjectInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/7/19 15:25
 */
@RestController
@RequestMapping("/system/subject")
public class SubjectInfoController extends BaseController {

    @Autowired
    private SubjectInfoService subjectInfoService;

    /**
     * 年级列表接口
     * @param params
     * @return
     */
    @GetMapping
    public Result list(@RequestParam Map params) {
        return subjectInfoService.pagination(params);
    }
}
