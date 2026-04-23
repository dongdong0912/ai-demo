package com.example.usermanagement.controller;

import com.example.usermanagement.dao.StudentDao;
import com.example.usermanagement.dao.GradeClassDao;
import com.example.usermanagement.dao.TeacherDao;
import com.example.usermanagement.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final UserDao userDao;
    private final TeacherDao teacherDao;
    private final StudentDao studentDao;
    private final GradeClassDao gradeClassDao;

    public DashboardController(UserDao userDao, TeacherDao teacherDao, 
                               StudentDao studentDao, GradeClassDao gradeClassDao) {
        this.userDao = userDao;
        this.teacherDao = teacherDao;
        this.studentDao = studentDao;
        this.gradeClassDao = gradeClassDao;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.debug("获取Dashboard统计数据");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userDao.count());
        stats.put("totalTeachers", teacherDao.count());
        stats.put("totalStudents", studentDao.count());
        stats.put("totalClasses", gradeClassDao.count());
        
        // 活跃状态统计
        stats.put("activeStudents", studentDao.countByStatus(1));
        stats.put("activeTeachers", teacherDao.countByStatus(1));
        stats.put("activeClasses", gradeClassDao.countByStatus(1));
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", stats);
        
        log.info("Dashboard统计数据获取成功");
        return ResponseEntity.ok(response);
    }
}
