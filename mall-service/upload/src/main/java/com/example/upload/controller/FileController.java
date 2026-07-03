package com.example.upload.controller;

import com.example.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/upload")
public class FileController {

    @Value("${file.upload-dir:D:/code/mall-platform/mall-service/uploads}")
    private String uploadDir;

    @Value("${file.access-url:http://localhost:8003/files}")
    private String accessUrl;

    /**
     * 单个文件上传
     */
    @PostMapping("/single")
    public Result<Map<String, String>> uploadSingle(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "image") String type) {
        try {
            String filePath = saveFile(file, type);
            String url = accessUrl + "/" + filePath;
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("path", filePath);
            result.put("originalName", file.getOriginalFilename());
            result.put("size", String.valueOf(file.getSize()));
            return Result.success(result);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 多个文件上传
     */
    @PostMapping("/multiple")
    public Result<List<Map<String, String>>> uploadMultiple(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "type", defaultValue = "image") String type) {
        List<Map<String, String>> results = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String filePath = saveFile(file, type);
                String url = accessUrl + "/" + filePath;
                Map<String, String> result = new HashMap<>();
                result.put("url", url);
                result.put("path", filePath);
                result.put("originalName", file.getOriginalFilename());
                result.put("size", String.valueOf(file.getSize()));
                results.add(result);
            } catch (IOException e) {
                log.error("文件上传失败: {}", file.getOriginalFilename(), e);
            }
        }
        return Result.success(results);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{type}/{date}/{filename}")
    public Result<Void> deleteFile(
            @PathVariable String type,
            @PathVariable String date,
            @PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir, type, date, filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return Result.success();
            }
            return Result.error("文件不存在");
        } catch (IOException e) {
            log.error("文件删除失败", e);
            return Result.error("文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 保存文件
     */
    private String saveFile(MultipartFile file, String type) throws IOException {
        // 创建日期目录
        String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Path uploadPath = Paths.get(uploadDir, type, dateDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 生成文件名
        String originalName = file.getOriginalFilename();
        String extension = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf("."))
                : "";
        String newFileName = UUID.randomUUID().toString().replace("-", "") + extension;

        // 保存文件
        Path filePath = uploadPath.resolve(newFileName);
        file.transferTo(filePath.toFile());

        // 返回相对路径
        return type + "/" + dateDir + "/" + newFileName;
    }
}
