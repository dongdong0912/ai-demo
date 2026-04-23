package com.example.usermanagement.config;

import com.example.usermanagement.entity.GradeClass;
import com.example.usermanagement.entity.Student;
import com.example.usermanagement.entity.Teacher;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.dao.GradeClassDao;
import com.example.usermanagement.dao.StudentDao;
import com.example.usermanagement.dao.TeacherDao;
import com.example.usermanagement.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class DataInitializer {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Bean
    public CommandLineRunner initData(TeacherDao teacherDao, UserDao userDao,
                                     GradeClassDao gradeClassDao, StudentDao studentDao) {
        return args -> {
            // 初始化测试账号
            initTestUsers(userDao);

            // 初始化老师数据
            initTeacherData(teacherDao);

            // 初始化班级数据
            initClassData(gradeClassDao, teacherDao);

            // 初始化学生数据
            initStudentData(studentDao, gradeClassDao);
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
        admin.setRealName("系统管理员");
        admin.setGender("男");
        admin.setDepartment("信息中心");
        admin.setStatus(1);
        admin.setEmail("admin@school.edu.cn");
        admin.setPhone("13800000000");
        userDao.save(admin);

        // 教师账号
        User teacher = new User();
        teacher.setUsername("teacher");
        teacher.setPassword(passwordEncoder.encode("123456"));
        teacher.setRole("USER");
        teacher.setRealName("张老师");
        teacher.setGender("女");
        teacher.setDepartment("教务处");
        teacher.setStatus(1);
        teacher.setEmail("teacher@school.edu.cn");
        teacher.setPhone("13800000001");
        userDao.save(teacher);

        // 学生账号
        User student = new User();
        student.setUsername("student");
        student.setPassword(passwordEncoder.encode("123456"));
        student.setRole("USER");
        student.setRealName("李同学");
        student.setGender("男");
        student.setDepartment("学生处");
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
        List<Teacher> teachers = new ArrayList<>();
        String[] subjects = {"语文", "数学", "英语", "物理", "化学", "生物", "历史", "地理", "政治", "音乐", "体育", "美术", "信息技术"};
        String[] titles = {"初级教师", "二级教师", "一级教师", "高级教师", "正高级教师"};
        String[] genders = {"男", "女"};
        String[] remarks = {"优秀教师", "骨干教师", "新入职教师", "教学能手", "班主任"};
        String[] cities = {"北京市", "上海市", "广州市", "深圳市", "杭州市", "南京市", "成都市", "武汉市"};

        for (int i = 1; i <= 100; i++) {
            Teacher teacher = new Teacher();
            teacher.setName("老师" + String.format("%03d", i));
            teacher.setGender(genders[i % 2]);
            teacher.setSubject(subjects[i % subjects.length]);
            teacher.setTitle(titles[i % titles.length]);
            teacher.setIdCard("110101" + (1980 + i % 20) + String.format("%04d", i + 1).substring(0, 4) + String.format("%04d", i));
            teacher.setPhone("138" + String.format("%08d", i));
            teacher.setEmail("teacher" + i + "@school.edu.cn");
            teacher.setAddress(cities[i % cities.length] + "某某路" + (i * 3) + "号");
            teacher.setBirthDate(LocalDate.of(1975 + i % 15, (i % 12) + 1, (i % 28) + 1));
            teacher.setEntryDate(LocalDate.of(2000 + i % 20, 7 + (i % 6), 1));
            teacher.setRemark(remarks[i % remarks.length]);
            teacher.setPhoto("https://api.dicebear.com/7.x/avataaars/svg?seed=" + i);
            teacher.setStatus(i % 10 == 0 ? 0 : 1);
            teachers.add(teacher);
        }

        teacherDao.saveAll(teachers);
        log.info("成功插入 {} 条老师数据", teachers.size());
    }

    private void initClassData(GradeClassDao gradeClassDao, TeacherDao teacherDao) {
        if (gradeClassDao.count() > 0) {
            log.info("数据库中已有 {} 条班级数据，跳过班级数据初始化", gradeClassDao.count());
            return;
        }

        log.info("开始初始化班级测试数据...");
        List<Teacher> teachers = teacherDao.findAll();
        List<GradeClass> classes = new ArrayList<>();

        // 2024级高一
        classes.add(createClass("高一(1)班", "2024级高一", teachers.size() > 0 ? teachers.get(0).getId() : null));
        classes.add(createClass("高一(2)班", "2024级高一", teachers.size() > 1 ? teachers.get(1).getId() : null));
        classes.add(createClass("高一(3)班", "2024级高一", teachers.size() > 2 ? teachers.get(2).getId() : null));

        // 2023级高二
        classes.add(createClass("高二(1)班", "2023级高二", teachers.size() > 3 ? teachers.get(3).getId() : null));
        classes.add(createClass("高二(2)班", "2023级高二", teachers.size() > 4 ? teachers.get(4).getId() : null));

        // 2022级高三
        classes.add(createClass("高三(1)班", "2022级高三", teachers.size() > 5 ? teachers.get(5).getId() : null));
        classes.add(createClass("高三(2)班", "2022级高三", teachers.size() > 6 ? teachers.get(6).getId() : null));
        classes.add(createClass("高三(3)班", "2022级高三", teachers.size() > 7 ? teachers.get(7).getId() : null));

        gradeClassDao.saveAll(classes);
        log.info("成功插入 {} 条班级数据", classes.size());
    }

    private GradeClass createClass(String className, String gradeName, Long headTeacherId) {
        GradeClass gradeClass = new GradeClass();
        gradeClass.setClassName(className);
        gradeClass.setGradeName(gradeName);
        gradeClass.setHeadTeacherId(headTeacherId);
        gradeClass.setMaxStudents(50);
        gradeClass.setStatus(1);
        return gradeClass;
    }

    private void initStudentData(StudentDao studentDao, GradeClassDao gradeClassDao) {
        if (studentDao.count() > 0) {
            log.info("数据库中已有 {} 条学生数据，跳过学生数据初始化", studentDao.count());
            return;
        }

        log.info("开始初始化学生测试数据...");
        List<GradeClass> classes = gradeClassDao.findAll();
        if (classes.isEmpty()) {
            log.warn("没有班级数据，跳过学生数据初始化");
            return;
        }

        List<Student> students = new ArrayList<>();
        String[] genders = {"男", "女"};
        String[] surnames = {"王", "李", "张", "刘", "陈", "杨", "黄", "赵", "周", "吴", "徐", "孙", "马", "朱", "胡", "郭", "何", "林", "罗", "高"};
        String[] givenNames = {"伟", "芳", "娜", "敏", "静", "丽", "强", "磊", "军", "洋", "勇", "艳", "杰", "涛", "明", "超", "秀英", "桂英", "建华", "志强"};
        String[] nations = {"汉族", "满族", "回族", "蒙古族", "藏族", "壮族", "维吾尔族", "土家族"};
        String[] cities = {"北京市", "上海市", "广州市", "深圳市", "杭州市", "南京市", "成都市", "武汉市"};

        int studentIndex = 1;
        for (GradeClass gradeClass : classes) {
            int studentsInClass = 6 + (int)(Math.random() * 3);
            String yearCode = gradeClass.getGradeName().substring(0, 4);
            int classNum = extractClassNumber(gradeClass.getClassName());
            int gradeYear = Integer.parseInt(yearCode);

            for (int i = 0; i < studentsInClass; i++) {
                Student student = new Student();
                String studentNo = yearCode + String.format("%02d", classNum) + String.format("%02d", i + 1);
                student.setStudentNo(studentNo);
                
                String name = surnames[(studentIndex + i) % surnames.length] + 
                             givenNames[(studentIndex + i * 2) % givenNames.length];
                student.setName(name);
                student.setGender(genders[(studentIndex + i) % 2]);
                student.setIdCard("110101" + (gradeYear - 15) + String.format("%04d", studentIndex + i).substring(0, 4) + String.format("%04d", studentIndex));
                student.setClassId(gradeClass.getId());
                student.setPhone("139" + String.format("%08d", studentIndex + i));
                student.setParentPhone("138" + String.format("%08d", 10000 + studentIndex + i));
                student.setParentName(surnames[(studentIndex + i + 3) % surnames.length] + givenNames[(studentIndex + i) % givenNames.length]);
                student.setAddress(cities[(studentIndex + i) % cities.length] + "某某小区" + (studentIndex + i) + "号");
                student.setBirthDate(LocalDate.of(gradeYear - 15, (i % 12) + 1, (i % 28) + 1));
                student.setNation(nations[(studentIndex + i) % nations.length]);
                student.setNativePlace(cities[(studentIndex + i) % cities.length]);
                student.setEnrollmentDate(LocalDate.of(gradeYear, 9, 1));
                student.setPhoto("https://api.dicebear.com/7.x/avataaars/svg?seed=student" + (studentIndex + i));
                
                int statusRand = (studentIndex + i) % 20;
                if (statusRand == 0) {
                    student.setStatus(0);
                } else if (statusRand == 1) {
                    student.setStatus(2);
                } else {
                    student.setStatus(1);
                }
                
                students.add(student);
            }
            studentIndex += studentsInClass;
        }

        studentDao.saveAll(students);
        log.info("成功插入 {} 条学生数据", students.size());
    }

    private int extractClassNumber(String className) {
        // 从 "高一(1)班" 提取数字 1
        int start = className.indexOf('(') + 1;
        int end = className.indexOf(')');
        return Integer.parseInt(className.substring(start, end));
    }
}
