package com.example.usermanagement.controller;

import com.example.usermanagement.entity.GradeClass;
import com.example.usermanagement.service.GradeClassService;
import com.example.usermanagement.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "*")
public class GradeClassController {

    private final GradeClassService gradeClassService;
    private final StudentService studentService;

    public GradeClassController(GradeClassService gradeClassService, StudentService studentService) {
        this.gradeClassService = gradeClassService;
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllClasses() {
        log.debug("获取班级列表");
        List<GradeClass> classes = gradeClassService.getAllClasses();
        log.info("成功获取班级列表, 共 {} 条数据", classes.size());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", classes);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/with-count")
    public ResponseEntity<Map<String, Object>> getClassesWithCount() {
        log.debug("获取班级列表(含学生人数)");
        List<GradeClass> classes = gradeClassService.getAllClasses();
        
        List<Map<String, Object>> result = classes.stream().map(c -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", c.getId());
            item.put("className", c.getClassName());
            item.put("gradeName", c.getGradeName());
            item.put("headTeacherId", c.getHeadTeacherId());
            item.put("maxStudents", c.getMaxStudents());
            item.put("status", c.getStatus());
            item.put("createTime", c.getCreateTime());
            item.put("updateTime", c.getUpdateTime());
            item.put("studentCount", studentService.countActiveByClassId(c.getId()));
            return item;
        }).collect(java.util.stream.Collectors.toList());
        
        log.info("成功获取班级列表(含学生人数), 共 {} 条数据", result.size());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getClassById(@PathVariable Long id) {
        log.debug("根据ID获取班级: {}", id);
        return gradeClassService.getClassById(id)
                .map(gradeClass -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", 200);
                    response.put("message", "success");
                    response.put("data", gradeClass);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    log.warn("班级不存在, ID: {}", id);
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", 404);
                    response.put("message", "班级不存在");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createClass(@RequestBody GradeClass gradeClass) {
        log.info("创建班级: {}", gradeClass.getClassName());
        GradeClass createdClass = gradeClassService.createClass(gradeClass);
        log.info("班级创建成功, ID: {}", createdClass.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 201);
        response.put("message", "创建成功");
        response.put("data", createdClass);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateClass(@PathVariable Long id, @RequestBody GradeClass gradeClass) {
        log.info("更新班级, ID: {}", id);
        GradeClass updatedClass = gradeClassService.updateClass(id, gradeClass);
        log.info("班级更新成功, ID: {}", id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", updatedClass);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteClass(@PathVariable Long id) {
        log.info("删除班级, ID: {}", id);
        gradeClassService.deleteClass(id);
        log.info("班级删除成功, ID: {}", id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}
