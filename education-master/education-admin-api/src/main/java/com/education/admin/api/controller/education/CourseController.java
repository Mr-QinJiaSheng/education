package com.education.admin.api.controller.education;

import com.education.common.annotation.Param;
import com.education.common.annotation.ParamsType;
import com.education.common.annotation.ParamsValidate;
import com.education.common.base.BaseController;
import com.education.common.utils.Result;
import com.education.service.education.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 课程管理接口
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/7/19 21:01
 */
@RestController
@RequestMapping("/system/course")
public class CourseController extends BaseController {

    @Autowired
    private CourseService courseService;

    /**
     * 课程列表
     * @param params
     * @return
     */
    @GetMapping
    public Result list(@RequestParam Map params) {
        return courseService.pagination(params);
    }

    /**
     * 添加或修改课程
     * @param params
     * @return
     */
    @PostMapping
    @ParamsValidate(params = {
        @Param(name = "name", message = "请输入课程名称"),
        @Param(name = "head_img", message = "请上传课程封面图"),
        @Param(name = "school_type", message = "请选择课程阶段"),
        @Param(name = "grade_type", message = "请选择课程所属年级")
    }, paramsType = ParamsType.JSON_DATA)
    public Result saveOrUpdate(@RequestBody Map params) {
        return courseService.saveOrUpdate(params);
    }

    /**
     * 根据id 删除课程
     * @param courseId
     * @return
     */
    @DeleteMapping
    public Result deleteById(@RequestParam Integer courseId) {
        return courseService.deleteById(courseId);
    }
}
