package com.example.usermanagement.config;

import com.example.usermanagement.entity.Course;
import com.example.usermanagement.entity.GradeClass;
import com.example.usermanagement.entity.Student;
import com.example.usermanagement.entity.Teacher;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.dao.CourseDao;
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
                                     GradeClassDao gradeClassDao, StudentDao studentDao,
                                     CourseDao courseDao) {
        return args -> {
            // 初始化测试账号
            initTestUsers(userDao);

            // 初始化老师数据
            initTeacherData(teacherDao);

            // 初始化班级数据
            initClassData(gradeClassDao, teacherDao);

            // 初始化学生数据
            initStudentData(studentDao, gradeClassDao);

            // 初始化课程数据
            initCourseData(courseDao, teacherDao);
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
            log.info("数据库中已有 {} 条老师数据，检查并修复缺失的职称字段...", teacherDao.count());
            // 修复已有数据中缺失的title
            fixMissingTitles(teacherDao);
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

    /**
     * 修复缺失职称字段的教师数据
     */
    private void fixMissingTitles(TeacherDao teacherDao) {
        String[] titles = {"初级教师", "二级教师", "一级教师", "高级教师", "正高级教师"};
        List<Teacher> allTeachers = teacherDao.findAll();
        List<Teacher> needFix = new ArrayList<>();
        int fixedCount = 0;

        for (int i = 0; i < allTeachers.size(); i++) {
            Teacher t = allTeachers.get(i);
            if (t.getTitle() == null || t.getTitle().isEmpty()) {
                t.setTitle(titles[i % titles.length]);
                needFix.add(t);
                fixedCount++;
            }
        }

        if (fixedCount > 0) {
            teacherDao.saveAll(needFix);
            log.info("成功修复 {} 条教师的职称数据", fixedCount);
        } else {
            log.info("所有教师的职称数据完整，无需修复");
        }
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

    private void initCourseData(CourseDao courseDao, TeacherDao teacherDao) {
        if (courseDao.count() > 0) {
            log.info("数据库中已有 {} 条课程数据，跳过课程数据初始化", courseDao.count());
            return;
        }

        log.info("开始初始化课程测试数据...");
        List<Teacher> teachers = teacherDao.findAll();
        List<Course> courses = new ArrayList<>();

        // 必修课
        courses.add(createCourse("语文", "YW101", "必修", 6.0, 108, "语言文学", 
                teachers.size() > 0 ? teachers.get(0).getId() : null, "培养学生的语言文字运用能力"));
        courses.add(createCourse("数学", "SX101", "必修", 6.0, 108, "数理科学",
                teachers.size() > 1 ? teachers.get(1).getId() : null, "培养学生的逻辑思维和运算能力"));
        courses.add(createCourse("英语", "YY101", "必修", 5.0, 90, "外语",
                teachers.size() > 2 ? teachers.get(2).getId() : null, "培养学生的英语综合运用能力"));
        courses.add(createCourse("物理", "WL101", "必修", 4.0, 72, "数理科学",
                teachers.size() > 3 ? teachers.get(3).getId() : null, "培养学生的物理思维和实验能力"));
        courses.add(createCourse("化学", "HX101", "必修", 4.0, 72, "数理科学",
                teachers.size() > 4 ? teachers.get(4).getId() : null, "培养学生的化学实验和理论分析能力"));
        courses.add(createCourse("生物", "SW101", "必修", 3.0, 54, "生命科学",
                teachers.size() > 5 ? teachers.get(5).getId() : null, "培养学生的生命科学素养"));
        courses.add(createCourse("历史", "LS101", "必修", 3.0, 54, "人文社科",
                teachers.size() > 6 ? teachers.get(6).getId() : null, "培养学生的历史意识和文化素养"));
        courses.add(createCourse("地理", "DL101", "必修", 3.0, 54, "人文社科",
                teachers.size() > 7 ? teachers.get(7).getId() : null, "培养学生的地理素养和环保意识"));
        courses.add(createCourse("政治", "ZZ101", "必修", 3.0, 54, "人文社科",
                teachers.size() > 8 ? teachers.get(8).getId() : null, "培养学生的政治素养和公民意识"));

        // 选修课
        courses.add(createCourse("音乐鉴赏", "YY201", "选修", 2.0, 36, "艺术",
                teachers.size() > 9 ? teachers.get(9).getId() : null, "提高学生的音乐审美能力"));
        courses.add(createCourse("美术鉴赏", "MS201", "选修", 2.0, 36, "艺术",
                teachers.size() > 10 ? teachers.get(10).getId() : null, "提高学生的美术鉴赏能力"));
        courses.add(createCourse("体育与健康", "TY201", "必修", 3.0, 54, "体育",
                teachers.size() > 11 ? teachers.get(11).getId() : null, "增强学生体质和健康意识"));
        courses.add(createCourse("信息技术", "XX201", "选修", 2.0, 36, "信息技术",
                teachers.size() > 12 ? teachers.get(12).getId() : null, "培养学生的信息素养和计算思维"));
        courses.add(createCourse("通用技术", "TY301", "选修", 2.0, 36, "信息技术",
                null, "培养学生的技术设计和实践能力"));
        courses.add(createCourse("研究性学习", "YJ401", "选修", 2.0, 36, "综合实践",
                null, "培养学生的研究能力和创新精神"));

        courseDao.saveAll(courses);
        log.info("成功插入 {} 条课程数据", courses.size());
    }

    private Course createCourse(String courseName, String courseCode, String type, 
                               Double credit, Integer totalHours, String category,
                               Long teacherId, String description) {
        Course course = new Course();
        course.setCourseName(courseName);
        course.setCourseCode(courseCode);
        course.setType(type);
        course.setCredit(credit);
        course.setTotalHours(totalHours);
        course.setCategory(category);
        course.setTeacherId(teacherId);
        course.setDescription(description);
        course.setStatus(1);
        return course;
    }
}
