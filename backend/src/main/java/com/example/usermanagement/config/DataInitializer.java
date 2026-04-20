package com.example.usermanagement.config;

import com.example.usermanagement.entity.Teacher;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.dao.TeacherDao;
import com.example.usermanagement.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
@Configuration
public class DataInitializer {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Bean
    public CommandLineRunner initData(TeacherDao teacherDao, UserDao userDao) {
        return args -> {
            // 初始化测试账号
            initTestUsers(userDao);

            // 初始化老师数据
            initTeacherData(teacherDao);
        };
    }

    private void initTestUsers(UserDao userDao) {
        if (userDao.count() > 0) {
            log.info("数据库中已有 {} 条用户数据，跳过账号初始化", userDao.count());
            return;
        }

        log.info("开始初始化测试账号...");

        // 管理员账号
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setRole("ADMIN");
        admin.setStatus(1);
        admin.setEmail("admin@school.edu.cn");
        admin.setPhone("13800000000");
        userDao.save(admin);

        // 教师账号
        User teacher = new User();
        teacher.setUsername("teacher");
        teacher.setPassword(passwordEncoder.encode("123456"));
        teacher.setRole("USER");
        teacher.setStatus(1);
        teacher.setEmail("teacher@school.edu.cn");
        teacher.setPhone("13800000001");
        userDao.save(teacher);

        // 学生账号
        User student = new User();
        student.setUsername("student");
        student.setPassword(passwordEncoder.encode("123456"));
        student.setRole("USER");
        student.setStatus(1);
        student.setEmail("student@school.edu.cn");
        student.setPhone("13800000002");
        userDao.save(student);

        log.info("成功初始化 3 个测试账号：admin/123456, teacher/123456, student/123456");
    }

    private void initTeacherData(TeacherDao teacherDao) {
        if (teacherDao.count() > 0) {
            log.info("数据库中已有 {} 条老师数据，跳过老师数据初始化", teacherDao.count());
            return;
        }

        log.info("开始初始化 100 条老师测试数据...");
        java.util.List<Teacher> teachers = new java.util.ArrayList<>();
        String[] subjects = {"语文", "数学", "英语", "物理", "化学", "生物", "历史", "地理", "政治", "音乐", "体育", "美术", "信息技术"};
        String[] genders = {"男", "女"};
        String[] remarks = {"优秀教师", "骨干教师", "新入职教师", "教学能手", "班主任"};

        for (int i = 1; i <= 100; i++) {
            Teacher teacher = new Teacher();
            teacher.setName("老师" + String.format("%03d", i));
            teacher.setGender(genders[i % 2]);
            teacher.setSubject(subjects[i % subjects.length]);
            teacher.setPhone("138" + String.format("%08d", i));
            teacher.setEmail("teacher" + i + "@school.edu.cn");
            teacher.setRemark(remarks[i % remarks.length]);
            teacher.setPhoto("https://api.dicebear.com/7.x/avataaars/svg?seed=" + i);
            teacher.setStatus(i % 10 == 0 ? 0 : 1);
            teachers.add(teacher);
        }

        teacherDao.saveAll(teachers);
        log.info("成功插入 {} 条老师数据", teachers.size());
    }
}
