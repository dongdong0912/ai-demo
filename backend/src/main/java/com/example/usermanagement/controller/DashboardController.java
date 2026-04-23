package com.example.usermanagement.controller;

import com.example.usermanagement.dao.StudentDao;
import com.example.usermanagement.dao.GradeClassDao;
import com.example.usermanagement.dao.TeacherDao;
import com.example.usermanagement.dao.UserDao;
import com.example.usermanagement.dao.CourseDao;
import com.example.usermanagement.entity.GradeClass;
import com.example.usermanagement.entity.Student;
import com.example.usermanagement.entity.Teacher;
import com.example.usermanagement.entity.Course;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final UserDao userDao;
    private final TeacherDao teacherDao;
    private final StudentDao studentDao;
    private final GradeClassDao gradeClassDao;
    private final CourseDao courseDao;

    public DashboardController(UserDao userDao, TeacherDao teacherDao,
                               StudentDao studentDao, GradeClassDao gradeClassDao,
                               CourseDao courseDao) {
        this.userDao = userDao;
        this.teacherDao = teacherDao;
        this.studentDao = studentDao;
        this.gradeClassDao = gradeClassDao;
        this.courseDao = courseDao;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.debug("获取Dashboard统计数据");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userDao.count());
        stats.put("totalTeachers", teacherDao.count());
        stats.put("totalStudents", studentDao.count());
        stats.put("totalClasses", gradeClassDao.count());
        stats.put("totalCourses", courseDao.count());

        // 活跃状态统计
        stats.put("activeStudents", studentDao.countByStatus(1));
        stats.put("activeTeachers", teacherDao.countByStatus(1));
        stats.put("activeClasses", gradeClassDao.countByStatus(1));
        stats.put("activeCourses", courseDao.countByStatus(1));

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", stats);

        log.info("Dashboard统计数据获取成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chart-data")
    public ResponseEntity<Map<String, Object>> getChartData() {
        log.debug("获取Dashboard图表数据");

        Map<String, Object> chartData = new HashMap<>();

        // 1. 各科目教师分布
        List<Teacher> teachers = teacherDao.findAll();
        Map<String, Long> subjectDist = teachers.stream()
                .filter(t -> t.getSubject() != null && t.getStatus() == 1)
                .collect(Collectors.groupingBy(Teacher::getSubject, Collectors.counting()));
        chartData.put("teacherSubjectDist", subjectDist);

        // 2. 各年级学生分布
        List<Student> students = studentDao.findAll();
        List<GradeClass> classes = gradeClassDao.findAll();
        Map<Long, String> classIdToGrade = classes.stream()
                .collect(Collectors.toMap(GradeClass::getId, GradeClass::getGradeName, (a, b) -> a));
        Map<String, Long> gradeStudentDist = students.stream()
                .filter(s -> s.getClassId() != null && s.getStatus() == 1)
                .collect(Collectors.groupingBy(
                        s -> classIdToGrade.getOrDefault(s.getClassId(), "未知"),
                        Collectors.counting()));
        chartData.put("gradeStudentDist", gradeStudentDist);

        // 3. 各班级学生人数
        List<Map<String, Object>> classStudentCount = new ArrayList<>();
        for (GradeClass gc : classes) {
            if (gc.getStatus() == 1) {
                Map<String, Object> item = new HashMap<>();
                item.put("className", gc.getClassName());
                item.put("gradeName", gc.getGradeName());
                item.put("count", students.stream()
                        .filter(s -> gc.getId().equals(s.getClassId()) && s.getStatus() == 1)
                        .count());
                classStudentCount.add(item);
            }
        }
        chartData.put("classStudentCount", classStudentCount);

        // 4. 课程分类统计
        List<Course> courses = courseDao.findAll();
        Map<String, Long> courseCategoryDist = courses.stream()
                .filter(c -> c.getCategory() != null && c.getStatus() == 1)
                .collect(Collectors.groupingBy(Course::getCategory, Collectors.counting()));
        chartData.put("courseCategoryDist", courseCategoryDist);

        // 5. 课程类型统计（必修/选修）
        Map<String, Long> courseTypeDist = courses.stream()
                .filter(c -> c.getType() != null && c.getStatus() == 1)
                .collect(Collectors.groupingBy(Course::getType, Collectors.counting()));
        chartData.put("courseTypeDist", courseTypeDist);

        // 6. 教师职称分布
        Map<String, Long> teacherTitleDist = teachers.stream()
                .filter(t -> t.getTitle() != null && t.getStatus() == 1)
                .collect(Collectors.groupingBy(Teacher::getTitle, Collectors.counting()));
        chartData.put("teacherTitleDist", teacherTitleDist);

        // 7. 学生状态分布
        Map<String, Long> studentStatusDist = new HashMap<>();
        studentStatusDist.put("在读", students.stream().filter(s -> s.getStatus() == 1).count());
        studentStatusDist.put("休学", students.stream().filter(s -> s.getStatus() == 0).count());
        studentStatusDist.put("毕业", students.stream().filter(s -> s.getStatus() == 2).count());
        studentStatusDist.put("退学", students.stream().filter(s -> s.getStatus() == 3).count());
        chartData.put("studentStatusDist", studentStatusDist);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", chartData);

        return ResponseEntity.ok(response);
    }
}
