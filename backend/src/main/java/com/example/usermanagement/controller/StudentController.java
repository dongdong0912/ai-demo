package com.example.usermanagement.controller;

import com.example.usermanagement.entity.Student;
import com.example.usermanagement.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStudents(
            @RequestParam(required = false) String keyword) {
        log.debug("获取学生列表, keyword={}", keyword);
        List<Student> students;
        if (keyword != null && !keyword.trim().isEmpty()) {
            students = studentService.searchStudents(keyword);
        } else {
            students = studentService.getAllStudents();
        }
        log.info("成功获取学生列表, 共 {} 条数据", students.size());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", students);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStudentById(@PathVariable Long id) {
        log.debug("根据ID获取学生: {}", id);
        return studentService.getStudentById(id)
                .map(student -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", 200);
                    response.put("message", "success");
                    response.put("data", student);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    log.warn("学生不存在, ID: {}", id);
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", 404);
                    response.put("message", "学生不存在");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<Map<String, Object>> getStudentsByClassId(@PathVariable Long classId) {
        log.debug("根据班级获取学生, classId={}", classId);
        List<Student> students = studentService.getStudentsByClassId(classId);
        log.info("成功获取班级学生列表, 共 {} 条数据", students.size());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", students);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createStudent(@RequestBody Student student) {
        log.info("创建学生: {}", student.getName());
        Student createdStudent = studentService.createStudent(student);
        log.info("学生创建成功, ID: {}", createdStudent.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 201);
        response.put("message", "创建成功");
        response.put("data", createdStudent);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        log.info("更新学生, ID: {}", id);
        Student updatedStudent = studentService.updateStudent(id, student);
        log.info("学生更新成功, ID: {}", id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", updatedStudent);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteStudent(@PathVariable Long id) {
        log.info("删除学生, ID: {}", id);
        studentService.deleteStudent(id);
        log.info("学生删除成功, ID: {}", id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}
