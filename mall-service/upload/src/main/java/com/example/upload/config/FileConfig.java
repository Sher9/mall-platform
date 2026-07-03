package com.example.upload.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 文件访问配置
 * 将上传的文件目录映射为可访问的 URL
 */
@Configuration
public class FileConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:D:/code/mall-platform/mall-service/uploads}")
    private String uploadDir;

    @Value("${file.access-path:/files/**}")
    private String accessPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /files/** 映射到上传目录
        String absolutePath = new java.io.File(uploadDir).getAbsolutePath();
        registry.addResourceHandler(accessPath)
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
