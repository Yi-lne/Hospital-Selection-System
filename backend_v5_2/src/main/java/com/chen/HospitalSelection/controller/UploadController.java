package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.util.FileUploadUtil;
import com.chen.HospitalSelection.util.JwtUtil;
import com.chen.HospitalSelection.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传接口
 * 基础路径：/api/upload
 *
 * @author chen
 * @since 2025-01-31
 */
@Slf4j
@RestController
@RequestMapping("/upload")
@Api(tags = "文件上传")
public class UploadController {

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 上传图片
     * 接口路径：POST /api/upload/image
     * 是否需要登录：是
     *
     * @param file 图片文件
     * @return 图片访问URL
     */
    @PostMapping("/image")
    @ApiOperation("上传图片")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file,
                                                   HttpServletRequest request) {
        try {
            // 验证用户登录（可选，根据需求决定是否需要登录）
            // Long userId = getCurrentUserId(request);

            // 上传图片
            String imageUrl = fileUploadUtil.uploadImage(file, "image");

            Map<String, String> data = new HashMap<>();
            data.put("url", imageUrl);
            data.put("filename", file.getOriginalFilename());
            data.put("size", String.valueOf(file.getSize()));

            log.info("图片上传成功，文件名：{}，URL：{}", file.getOriginalFilename(), imageUrl);
            return Result.success(data, "图片上传成功");
        } catch (IllegalArgumentException e) {
            log.error("图片上传失败，参数错误：{}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("图片上传失败，系统错误", e);
            return Result.error(500, "图片上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传头像
     * 接口路径：POST /api/upload/avatar
     * 是否需要登录：是
     *
     * @param file 头像图片文件
     * @return 头像访问URL
     */
    @PostMapping("/avatar")
    @ApiOperation("上传头像")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file,
                                                    HttpServletRequest request) {
        try {
            // 验证用户登录
            Long userId = getCurrentUserId(request);
            log.info("用户{}上传头像", userId);

            // 上传头像
            String avatarUrl = fileUploadUtil.uploadImage(file, "avatar");

            Map<String, String> data = new HashMap<>();
            data.put("url", avatarUrl);
            data.put("filename", file.getOriginalFilename());
            data.put("size", String.valueOf(file.getSize()));

            log.info("头像上传成功，用户ID：{}，URL：{}", userId, avatarUrl);
            return Result.success(data, "头像上传成功");
        } catch (IllegalArgumentException e) {
            log.error("头像上传失败，参数错误：{}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("头像上传失败，系统错误", e);
            return Result.error(500, "头像上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传文件
     * 接口路径：POST /api/upload/file
     * 是否需要登录：是
     *
     * @param file 文件
     * @return 文件访问URL
     */
    @PostMapping("/file")
    @ApiOperation("上传文件")
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file,
                                                  @RequestParam(required = false, defaultValue = "document") String fileType,
                                                  HttpServletRequest request) {
        try {
            // 验证用户登录（可选）
            // Long userId = getCurrentUserId(request);

            // 上传文件
            String fileUrl = fileUploadUtil.uploadFile(file, fileType);

            Map<String, String> data = new HashMap<>();
            data.put("url", fileUrl);
            data.put("filename", file.getOriginalFilename());
            data.put("size", String.valueOf(file.getSize()));

            log.info("文件上传成功，文件名：{}，URL：{}", file.getOriginalFilename(), fileUrl);
            return Result.success(data, "文件上传成功");
        } catch (IllegalArgumentException e) {
            log.error("文件上传失败，参数错误：{}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("文件上传失败，系统错误", e);
            return Result.error(500, "文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 批量上传图片
     * 接口路径：POST /api/upload/images
     * 是否需要登录：是
     *
     * @param files 图片文件数组
     * @return 图片访问URL列表
     */
    @PostMapping("/images")
    @ApiOperation("批量上传图片")
    public Result<Map<String, Object>> uploadImages(@RequestParam("files") MultipartFile[] files,
                                                    HttpServletRequest request) {
        try {
            // 验证用户登录（可选）
            // Long userId = getCurrentUserId(request);

            if (files == null || files.length == 0) {
                return Result.error(400, "请选择要上传的文件");
            }

            if (files.length > 9) {
                return Result.error(400, "最多支持同时上传9张图片");
            }

            String[] urls = new String[files.length];
            int successCount = 0;

            for (int i = 0; i < files.length; i++) {
                try {
                    urls[i] = fileUploadUtil.uploadImage(files[i], "image");
                    successCount++;
                } catch (Exception e) {
                    log.error("第{}张图片上传失败：{}", i + 1, e.getMessage());
                    urls[i] = null;
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("urls", urls);
            data.put("successCount", successCount);
            data.put("totalCount", files.length);

            log.info("批量上传图片完成，成功{}/{}", successCount, files.length);
            return Result.success(data, "批量上传完成");
        } catch (Exception e) {
            log.error("批量上传失败", e);
            return Result.error(500, "批量上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前登录用户ID
     *
     * @param request HTTP请求对象
     * @return 用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                throw new RuntimeException("用户未登录");
            }
            return userId;
        } catch (Exception e) {
            throw new RuntimeException("获取用户信息失败，请重新登录");
        }
    }
}
