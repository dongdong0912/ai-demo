package com.example.usermanagement.excel.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.example.usermanagement.dao.CourseDao;
import com.example.usermanagement.dao.GradeClassDao;
import com.example.usermanagement.dao.StudentDao;
import com.example.usermanagement.dao.TeacherDao;
import com.example.usermanagement.dao.UserDao;
import com.example.usermanagement.entity.*;
import com.example.usermanagement.excel.dto.*;
import com.example.usermanagement.excel.listener.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private GradeClassDao gradeClassDao;
    @Autowired
    private CourseDao courseDao;

    // ==================== 导出 ====================

    public void exportUsers(HttpServletResponse response) throws Exception {
        setResponseHeader(response, "用户列表.xlsx");
        List<User> list = userDao.findAll();
        List<UserExcelDTO> data = convertToUserDto(list);
        EasyExcel.write(response.getOutputStream(), UserExcelDTO.class).sheet("用户数据").doWrite(data);
    }

    public void exportTeachers(HttpServletResponse response) throws Exception {
        setResponseHeader(response, "教师列表.xlsx");
        List<Teacher> list = teacherDao.findAll();
        List<TeacherExcelDTO> data = convertToTeacherDto(list);
        EasyExcel.write(response.getOutputStream(), TeacherExcelDTO.class).sheet("教师数据").doWrite(data);
    }

    public void exportStudents(HttpServletResponse response) throws Exception {
        setResponseHeader(response, "学生列表.xlsx");
        List<Student> list = studentDao.findAll();
        List<StudentExcelDTO> data = convertToStudentDto(list);
        EasyExcel.write(response.getOutputStream(), StudentExcelDTO.class).sheet("学生数据").doWrite(data);
    }

    public void exportClasses(HttpServletResponse response) throws Exception {
        setResponseHeader(response, "班级列表.xlsx");
        List<GradeClass> list = gradeClassDao.findAll();
        List<ClassExcelDTO> data = convertToClassDto(list);
        EasyExcel.write(response.getOutputStream(), ClassExcelDTO.class).sheet("班级数据").doWrite(data);
    }

    public void exportCourses(HttpServletResponse response) throws Exception {
        setResponseHeader(response, "课程列表.xlsx");
        List<Course> list = courseDao.findAll();
        List<CourseExcelDTO> data = convertToCourseDto(list);
        EasyExcel.write(response.getOutputStream(), CourseExcelDTO.class).sheet("课程数据").doWrite(data);
    }

    // ==================== 模板下载 ====================

    public void downloadUserTemplate(HttpServletResponse response) throws Exception {
        setResponseHeader(response, "用户导入模板.xlsx");
        EasyExcel.write(response.getOutputStream(), UserExcelDTO.class).sheet("用户模板").doWrite(new ArrayList<>());
    }

    public void downloadTeacherTemplate(HttpServletResponse response) throws Exception {
        setResponseHeader(response, "教师导入模板.xlsx");
        EasyExcel.write(response.getOutputStream(), TeacherExcelDTO.class).sheet("教师模板").doWrite(new ArrayList<>());
    }

    public void downloadStudentTemplate(HttpServletResponse response) throws Exception {
        setResponseHeader(response, "学生导入模板.xlsx");
        EasyExcel.write(response.getOutputStream(), StudentExcelDTO.class).sheet("学生模板").doWrite(new ArrayList<>());
    }

    public void downloadClassTemplate(HttpServletResponse response) throws Exception {
        setResponseHeader(response, "班级导入模板.xlsx");
        EasyExcel.write(response.getOutputStream(), ClassExcelDTO.class).sheet("班级模板").doWrite(new ArrayList<>());
    }

    public void downloadCourseTemplate(HttpServletResponse response) throws Exception {
        setResponseHeader(response, "课程导入模板.xlsx");
        EasyExcel.write(response.getOutputStream(), CourseExcelDTO.class).sheet("课程模板").doWrite(new ArrayList<>());
    }

    // ==================== 导入 ====================

    public ImportResult importUsers(byte[] fileBytes) {
        UserImportListener listener = new UserImportListener();
        EasyExcel.read(new ByteArrayInputStream(fileBytes), UserExcelDTO.class, listener).sheet().doRead();
        return saveAndResult(listener.getSuccessData(), listener.getErrors(), u -> userDao.save(u));
    }

    public ImportResult importTeachers(byte[] fileBytes) {
        TeacherImportListener listener = new TeacherImportListener();
        EasyExcel.read(new ByteArrayInputStream(fileBytes), TeacherExcelDTO.class, listener).sheet().doRead();
        return saveAndResult(listener.getSuccessData(), listener.getErrors(), t -> teacherDao.save(t));
    }

    public ImportResult importStudents(byte[] fileBytes) {
        StudentImportListener listener = new StudentImportListener();
        EasyExcel.read(new ByteArrayInputStream(fileBytes), StudentExcelDTO.class, listener).sheet().doRead();
        return saveAndResult(listener.getSuccessData(), listener.getErrors(), s -> studentDao.save(s));
    }

    public ImportResult importClasses(byte[] fileBytes) {
        ClassImportListener listener = new ClassImportListener();
        EasyExcel.read(new ByteArrayInputStream(fileBytes), ClassExcelDTO.class, listener).sheet().doRead();
        return saveAndResult(listener.getSuccessData(), listener.getErrors(), c -> gradeClassDao.save(c));
    }

    public ImportResult importCourses(byte[] fileBytes) {
        CourseImportListener listener = new CourseImportListener();
        EasyExcel.read(new ByteArrayInputStream(fileBytes), CourseExcelDTO.class, listener).sheet().doRead();
        return saveAndResult(listener.getSuccessData(), listener.getErrors(), c -> courseDao.save(c));
    }

    // ==================== 内部工具方法 ====================

    private interface EntitySaver<T> {
        T save(T entity);
    }

    @FunctionalInterface
    private interface Saver<T> {
        T save(T entity);
    }

    private <T> ImportResult saveAndResult(List<T> dataList, List<String> errors, java.util.function.Function<T, T> saver) {
        ImportResult result = new ImportResult();
        result.setTotalCount((dataList.size() + errors.size()) - 1);
        if (result.getTotalCount() <= 0) {
            result.setTotalCount(0);
        }
        result.setErrorCount(errors.size());

        for (T entity : dataList) {
            try {
                saver.apply(entity);
                result.incrementSuccessCount();
            } catch (Exception e) {
                errors.add("保存失败: " + e.getMessage());
                result.incrementErrorCount();
            }
        }
        result.setErrors(errors);
        return result;
    }

    private void setResponseHeader(HttpServletResponse response, String fileName) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
    }

    // ==================== 实体转换 DTO ====================

    private List<UserExcelDTO> convertToUserDto(List<User> users) {
        List<UserExcelDTO> list = new ArrayList<>();
        for (User u : users) {
            UserExcelDTO dto = new UserExcelDTO();
            dto.setUsername(u.getUsername());
            dto.setRealName(u.getRealName());
            dto.setGender(u.getGender());
            dto.setRole(u.getRole());
            dto.setEmail(u.getEmail());
            dto.setPhone(u.getPhone());
            dto.setDepartment(u.getDepartment());
            dto.setStatus(u.getStatus());
            list.add(dto);
        }
        return list;
    }

    private List<TeacherExcelDTO> convertToTeacherDto(List<Teacher> teachers) {
        List<TeacherExcelDTO> list = new ArrayList<>();
        for (Teacher t : teachers) {
            TeacherExcelDTO dto = new TeacherExcelDTO();
            dto.setName(t.getName());
            dto.setGender(t.getGender());
            dto.setSubject(t.getSubject());
            dto.setTitle(t.getTitle());
            dto.setIdCard(t.getIdCard());
            dto.setPhone(t.getPhone());
            dto.setEmail(t.getEmail());
            dto.setStatus(t.getStatus());
            list.add(dto);
        }
        return list;
    }

    private List<StudentExcelDTO> convertToStudentDto(List<Student> students) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<StudentExcelDTO> list = new ArrayList<>();
        for (Student s : students) {
            StudentExcelDTO dto = new StudentExcelDTO();
            dto.setStudentNo(s.getStudentNo());
            dto.setName(s.getName());
            dto.setGender(s.getGender());
            dto.setIdCard(s.getIdCard());
            dto.setClassId(s.getClassId());
            dto.setPhone(s.getPhone());
            dto.setParentPhone(s.getParentPhone());
            dto.setParentName(s.getParentName());
            dto.setNation(s.getNation());
            dto.setNativePlace(s.getNativePlace());
            dto.setBirthDate(s.getBirthDate() != null ? s.getBirthDate().format(fmt) : null);
            dto.setEnrollmentDate(s.getEnrollmentDate() != null ? s.getEnrollmentDate().format(fmt) : null);
            dto.setStatus(s.getStatus());
            list.add(dto);
        }
        return list;
    }

    private List<ClassExcelDTO> convertToClassDto(List<GradeClass> classes) {
        List<ClassExcelDTO> list = new ArrayList<>();
        for (GradeClass c : classes) {
            ClassExcelDTO dto = new ClassExcelDTO();
            dto.setClassName(c.getClassName());
            dto.setGradeName(c.getGradeName());
            dto.setHeadTeacherId(c.getHeadTeacherId());
            dto.setMaxStudents(c.getMaxStudents());
            dto.setStatus(c.getStatus());
            list.add(dto);
        }
        return list;
    }

    private List<CourseExcelDTO> convertToCourseDto(List<Course> courses) {
        List<CourseExcelDTO> list = new ArrayList<>();
        for (Course c : courses) {
            CourseExcelDTO dto = new CourseExcelDTO();
            dto.setCourseName(c.getCourseName());
            dto.setCourseCode(c.getCourseCode());
            dto.setType(c.getType());
            dto.setCredit(c.getCredit());
            dto.setTotalHours(c.getTotalHours());
            dto.setCategory(c.getCategory());
            dto.setTeacherId(c.getTeacherId());
            dto.setStatus(c.getStatus());
            list.add(dto);
        }
        return list;
    }

    // ==================== 导入结果封装类 ====================

    @lombok.Data
    public static class ImportResult {
        private int totalCount;
        private int successCount;
        private int errorCount;
        private List<String> errors = new ArrayList<>();

        public void incrementSuccessCount() { this.successCount++; }
        public void incrementErrorCount() { this.errorCount++; }
    }
}
