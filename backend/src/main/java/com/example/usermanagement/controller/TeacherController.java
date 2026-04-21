package com.example.usermanagement.controller;

import com.example.usermanagement.entity.Teacher;
import com.example.usermanagement.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "*")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTeachers(
            @RequestParam(required = false) String keyword) {
        log.debug("获取老师列表, keyword={}", keyword);
        List<Teacher> teachers;
        if (keyword != null && !keyword.trim().isEmpty()) {
            teachers = teacherService.searchTeachers(keyword);
        } else {
            teachers = teacherService.getAllTeachers();
        }
        log.info("成功获取老师列表, 共 {} 条数据", teachers.size());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", teachers);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTeacherById(@PathVariable Long id) {
        log.debug("根据ID获取老师: {}", id);
        return teacherService.getTeacherById(id)
                .map(teacher -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", 200);
                    response.put("message", "success");
                    response.put("data", teacher);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    log.warn("老师不存在, ID: {}", id);
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", 404);
                    response.put("message", "老师不存在");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTeacher(@RequestBody Teacher teacher) {
        log.info("创建老师: {}", teacher.getName());
        Teacher createdTeacher = teacherService.createTeacher(teacher);
        log.info("老师创建成功, ID: {}", createdTeacher.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 201);
        response.put("message", "创建成功");
        response.put("data", createdTeacher);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTeacher(@PathVariable Long id, @RequestBody Teacher teacher) {
        log.info("更新老师, ID: {}", id);
        Teacher updatedTeacher = teacherService.updateTeacher(id, teacher);
        log.info("老师更新成功, ID: {}", id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", updatedTeacher);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTeacher(@PathVariable Long id) {
        log.info("删除老师, ID: {}", id);
        teacherService.deleteTeacher(id);
        log.info("老师删除成功, ID: {}", id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}
