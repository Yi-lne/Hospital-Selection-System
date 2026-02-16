package com.chen.HospitalSelection.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传工具类
 * 处理文件上传、保存、删除等操作
 *
 * @author chen
 * @since 2025-01-31
 */
@Slf4j
@Component
public class FileUploadUtil {

    /**
     * 上传文件保存路径
     */
    @Value("${file.upload-path:D:/upload/hospital-selection}")
    private String uploadPath;

    /**
     * 文件访问URL前缀
     */
    @Value("${file.access-url:/uploads}")
    private String accessUrl;

    /**
     * 允许上传的图片格式
     */
    private static final String[] IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};

    /**
     * 最大文件大小（10MB）
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @param fileType 文件类型（avatar, document等）
     * @return 文件访问URL
     * @throws IOException 当文件保存失败时抛出异常
     */
    public String uploadFile(MultipartFile file, String fileType) throws IOException {
        // 验证文件
        validateFile(file);

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFilename = generateFilename(extension);

        // 构建保存路径
        String relativePath = buildRelativePath(fileType);
        String fullPath = uploadPath + File.separator + relativePath;
        Path filePath = Paths.get(fullPath, newFilename);

        // 确保目录存在
        Files.createDirectories(filePath.getParent());

        // 保存文件
        file.transferTo(filePath.toFile());

        // 构建访问URL
        String accessUrl = this.accessUrl + "/" + relativePath + "/" + newFilename;
        accessUrl = accessUrl.replace("\\", "/"); // 统一分隔符

        log.info("文件上传成功，原始文件名：{}，保存路径：{}", originalFilename, filePath);

        return accessUrl;
    }

    /**
     * 上传图片文件
     *
     * @param file 上传的图片文件
     * @param fileType 文件类型
     * @return 图片访问URL
     * @throws IOException 当文件保存失败时抛出异常
     */
    public String uploadImage(MultipartFile file, String fileType) throws IOException {
        // 验证是否为图片
        if (!isImageFile(file)) {
            throw new IllegalArgumentException("只支持上传图片文件（jpg、png、gif等）");
        }

        return uploadFile(file, fileType);
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件访问URL
     * @return 删除是否成功
     */
    public boolean deleteFile(String fileUrl) {
        try {
            // 从URL中提取相对路径
            String relativePath = fileUrl.replace(this.accessUrl, "");
            relativePath = relativePath.replace("/", File.separator);

            // 构建完整路径
            String fullPath = uploadPath + relativePath;

            File file = new File(fullPath);
            if (file.exists() && file.isFile()) {
                boolean deleted = file.delete();
                log.info("文件删除{}，文件路径：{}", deleted ? "成功" : "失败", fullPath);
                return deleted;
            }

            log.warn("文件不存在或不是文件，文件路径：{}", fullPath);
            return false;
        } catch (Exception e) {
            log.error("删除文件失败，文件URL：{}", fileUrl, e);
            return false;
        }
    }

    /**
     * 验证文件
     *
     * @param file 上传的文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过10MB");
        }

        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        if (!isValidExtension(extension)) {
            throw new IllegalArgumentException("不支持的文件格式");
        }
    }

    /**
     * 检查是否为图片文件
     *
     * @param file 上传的文件
     * @return 是否为图片
     */
    private boolean isImageFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }

        String extension = getFileExtension(originalFilename);
        for (String imageExt : IMAGE_EXTENSIONS) {
            if (imageExt.equalsIgnoreCase(extension)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查文件扩展名是否有效
     *
     * @param extension 文件扩展名
     * @return 是否有效
     */
    private boolean isValidExtension(String extension) {
        for (String validExt : IMAGE_EXTENSIONS) {
            if (validExt.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名（包含.）
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }

        return filename.substring(lastDotIndex);
    }

    /**
     * 生成新文件名
     *
     * @param extension 文件扩展名
     * @return 新文件名
     */
    private String generateFilename(String extension) {
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }

    /**
     * 构建相对保存路径
     *
     * @param fileType 文件类型
     * @return 相对路径
     */
    private String buildRelativePath(String fileType) {
        // 按日期分类存储
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return fileType + File.separator + datePath;
    }
}
