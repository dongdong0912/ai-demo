package com.example.usermanagement.controller;

import com.example.usermanagement.dto.ApiResponse;
import com.example.usermanagement.excel.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/excel")
@CrossOrigin(origins = "*")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    // ==================== 导出接口 ====================

    @GetMapping("/export/users")
    public void exportUsers(HttpServletResponse response) {
        try { excelService.exportUsers(response); } catch (Exception e) { setErrorResponse(response, "导出用户失败"); }
    }

    @GetMapping("/export/teachers")
    public void exportTeachers(HttpServletResponse response) {
        try { excelService.exportTeachers(response); } catch (Exception e) { setErrorResponse(response, "导出教师失败"); }
    }

    @GetMapping("/export/students")
    public void exportStudents(HttpServletResponse response) {
        try { excelService.exportStudents(response); } catch (Exception e) { setErrorResponse(response, "导出学生失败"); }
    }

    @GetMapping("/export/classes")
    public void exportClasses(HttpServletResponse response) {
        try { excelService.exportClasses(response); } catch (Exception e) { setErrorResponse(response, "导出班级失败"); }
    }

    @GetMapping("/export/courses")
    public void exportCourses(HttpServletResponse response) {
        try { excelService.exportCourses(response); } catch (Exception e) { setErrorResponse(response, "导出课程失败"); }
    }

    // ==================== 模板下载接口 ====================

    @GetMapping("/template/users")
    public void downloadUserTemplate(HttpServletResponse response) {
        try { excelService.downloadUserTemplate(response); } catch (Exception e) { setErrorResponse(response, "下载用户模板失败"); }
    }

    @GetMapping("/template/teachers")
    public void downloadTeacherTemplate(HttpServletResponse response) {
        try { excelService.downloadTeacherTemplate(response); } catch (Exception e) { setErrorResponse(response, "下载教师模板失败"); }
    }

    @GetMapping("/template/students")
    public void downloadStudentTemplate(HttpServletResponse response) {
        try { excelService.downloadStudentTemplate(response); } catch (Exception e) { setErrorResponse(response, "下载学生模板失败"); }
    }

    @GetMapping("/template/classes")
    public void downloadClassTemplate(HttpServletResponse response) {
        try { excelService.downloadClassTemplate(response); } catch (Exception e) { setErrorResponse(response, "下载班级模板失败"); }
    }

    @GetMapping("/template/courses")
    public void downloadCourseTemplate(HttpServletResponse response) {
        try { excelService.downloadCourseTemplate(response); } catch (Exception e) { setErrorResponse(response, "下载课程模板失败"); }
    }

    // ==================== 导入接口 ====================

    @PostMapping("/import/users")
    public ApiResponse<ExcelService.ImportResult> importUsers(@RequestParam("file") MultipartFile file) {
        return handleImport(file, excelService::importUsers);
    }

    @PostMapping("/import/teachers")
    public ApiResponse<ExcelService.ImportResult> importTeachers(@RequestParam("file") MultipartFile file) {
        return handleImport(file, excelService::importTeachers);
    }

    @PostMapping("/import/students")
    public ApiResponse<ExcelService.ImportResult> importStudents(@RequestParam("file") MultipartFile file) {
        return handleImport(file, excelService::importStudents);
    }

    @PostMapping("/import/classes")
    public ApiResponse<ExcelService.ImportResult> importClasses(@RequestParam("file") MultipartFile file) {
        return handleImport(file, excelService::importClasses);
    }

    @PostMapping("/import/courses")
    public ApiResponse<ExcelService.ImportResult> importCourses(@RequestParam("file") MultipartFile file) {
        return handleImport(file, excelService::importCourses);
    }

    // ==================== 内部工具 ====================

    private ApiResponse<ExcelService.ImportResult> handleImport(MultipartFile file,
            java.util.function.Function<byte[], ExcelService.ImportResult> importer) {
        if (file.isEmpty()) {
            return ApiResponse.error(400, "上传文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return ApiResponse.error(400, "只支持 .xlsx 或 .xls 格式文件");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            return ApiResponse.error(400, "文件大小不能超过10MB");
        }
        try {
            ExcelService.ImportResult result = importer.apply(file.getBytes());
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(500, "导入失败: " + e.getMessage());
        }
    }

    private void setErrorResponse(HttpServletResponse response, String message) {
        response.setStatus(500);
        response.setContentType("application/json;charset=utf-8");
        try {
            response.getWriter().write("{\"code\":500,\"message\":\"" + message + "\"}");
        } catch (Exception ignored) {}
    }
}
