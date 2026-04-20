package com.example.usermanagement.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileController {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.upload.base-url:http://localhost:8080/uploads}")
    private String baseUrl;

    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (file.isEmpty()) {
            response.put("code", 400);
            response.put("message", "请选择要上传的文件");
            return ResponseEntity.badRequest().body(response);
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            response.put("code", 400);
            response.put("message", "只能上传图片文件");
            return ResponseEntity.badRequest().body(response);
        }

        // 检查文件大小 (最大 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            response.put("code", 400);
            response.put("message", "文件大小不能超过 5MB");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 创建上传目录
            String datePath = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String uploadDir = uploadPath + File.separator + datePath;
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;
            
            // 保存文件
            Path filePath = Paths.get(uploadDir, newFilename);
            Files.copy(file.getInputStream(), filePath);
            
            // 返回访问URL
            String fileUrl = baseUrl + "/" + datePath + "/" + newFilename;
            
            log.info("文件上传成功: {}", fileUrl);
            
            response.put("code", 200);
            response.put("message", "上传成功");
            Map<String, String> data = new HashMap<>();
            data.put("url", fileUrl);
            data.put("filename", newFilename);
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            response.put("code", 500);
            response.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
