package com.example.usermanagement.controller;

import com.example.usermanagement.entity.Course;
import com.example.usermanagement.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCourses(
            @RequestParam(required = false) String keyword) {
        log.debug("获取课程列表, keyword={}", keyword);
        List<Course> courses;
        if (keyword != null && !keyword.trim().isEmpty()) {
            courses = courseService.searchCourses(keyword);
        } else {
            courses = courseService.getAllCourses();
        }
        log.info("成功获取课程列表, 共 {} 条数据", courses.size());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", courses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCourseById(@PathVariable Long id) {
        log.debug("根据ID获取课程: {}", id);
        return courseService.getCourseById(id)
                .map(course -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", 200);
                    response.put("message", "success");
                    response.put("data", course);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    log.warn("课程不存在, ID: {}", id);
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", 404);
                    response.put("message", "课程不存在");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCourse(@RequestBody Course course) {
        log.info("创建课程: {}", course.getCourseName());
        try {
            Course createdCourse = courseService.createCourse(course);
            log.info("课程创建成功, ID: {}", createdCourse.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 201);
            response.put("message", "创建成功");
            response.put("data", createdCourse);
            return ResponseEntity.status(201).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("课程创建失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        log.info("更新课程, ID: {}", id);
        try {
            Course updatedCourse = courseService.updateCourse(id, course);
            log.info("课程更新成功, ID: {}", id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", updatedCourse);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("课程更新失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable Long id) {
        log.info("删除课程, ID: {}", id);
        try {
            courseService.deleteCourse(id);
            log.info("课程删除成功, ID: {}", id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("课程删除失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", e.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }
}
