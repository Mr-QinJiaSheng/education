package com.education.admin.api.controller;
import com.baidu.ueditor.ActionEnter;
import com.education.common.utils.ObjectUtils;
import com.education.common.utils.PathKit;
import com.education.common.utils.Result;
import com.education.common.utils.ResultCode;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件上传接口
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/7/16 20:54
 */
@RestController
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    private static final int IMAGE_FILE = 1; // 图片文件
    private static final int VIDEO_FILE = 2; // 视频文件
    private static final int OTHER_FILE = 3; // 其它文件

    @Value("${file.upload}")
    private String baseUploadPath;

    private static final Set<String> imageContentType = new HashSet() {

        {
            add("image/jpeg");
            add("image/jpeg");
            add("image/gif");
            add("image/png");
            add("image/x-png");
        }
    };

    @RequestMapping(value = "/upload/{uploadFileType}", method = RequestMethod.POST)
    public Result upload(@RequestParam MultipartFile file, @PathVariable int uploadFileType) {
        Result result = this.checkImageFile(file);
        if (!result.isSuccess()) {
            return result;
        }
        String fileName = file.getOriginalFilename(); // 获取文件名称
        String suffix = "." + FilenameUtils.getExtension(fileName); // 获取文件的后缀
        String uploadPath = null;
        switch (uploadFileType) {
            case IMAGE_FILE:
                uploadPath = createImageFile(suffix);
                break;
            case VIDEO_FILE:
                break;
            case OTHER_FILE:
                break;
        }
        if (ObjectUtils.isNotEmpty(uploadPath)) {
            try {
                String filePath = baseUploadPath + uploadPath; // 文件路径
                File uploadFile = new File(filePath);
                if (!uploadFile.exists()) {
                    uploadFile.getParentFile().mkdirs();
                }
                file.transferTo(uploadFile); // 将上传的文件生成到uploadFile 文件目录下
                result.setCode(ResultCode.SUCCESS);
                result.setMessage("文件上传成功");
                result.setData(uploadPath);
            } catch (IOException e) {
                logger.error("文件上传异常", e);
                result.setCode(ResultCode.FAIL);
                result.setMessage("文件上传失败");
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 创建图片路径
     * @param suffix 图片文件的后缀
     * @return
     */
    private String createImageFile(String suffix) {
        return "/images/" + ObjectUtils.generateFileByTime() + ObjectUtils.generateUuId() + suffix;
    }

    /**
     * 校验文件类型和文件大小
     * @return
     */
    private Result checkImageFile(MultipartFile file) {
        String contentType = file.getContentType(); // 获取文件content-type
        if (!imageContentType.contains(contentType)) {
            return Result.fail(ResultCode.FAIL, "图片格式错误, 只能上传JPG/PNG 格式,请重新上传");
        }

        long fileSize = file.getSize(); //获取文件大小
        if (fileSize > 1024 * 1024) {
            return Result.fail(ResultCode.FAIL, "上传的图片不能超过1MB");
        }

        return new Result();
    }

    @DeleteMapping("deleteFile")
    public Result deleteFile(@RequestParam String url) {
        File file = new File(baseUploadPath + url);
        if (file.exists()) {
            file.delete();
            return Result.success(ResultCode.SUCCESS, "文件删除成功");
        }
        return Result.fail(ResultCode.FAIL, "文件不存在或已被删除");
    }


    @RequestMapping(value = "/ueditor/exec", method = { RequestMethod.GET, RequestMethod.POST})
    public String exec(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Content-Type", "text/html");
        return new ActionEnter(request, PathKit.getWebRootPath()).exec();
    }


}
