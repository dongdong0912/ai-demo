package com.example.usermanagement.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.example.usermanagement.dao.StudentDao;
import com.example.usermanagement.dao.CourseDao;
import com.example.usermanagement.entity.Student;
import com.example.usermanagement.entity.Course;
import com.example.usermanagement.entity.Score;
import com.example.usermanagement.excel.dto.ScoreExcelDTO;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ScoreImportListener implements ReadListener<ScoreExcelDTO> {
    
    private final List<Score> successData = new ArrayList<>();
    private final List<ErrorInfo> errors = new ArrayList<>();
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    
    public ScoreImportListener(StudentDao studentDao, CourseDao courseDao) {
        this.studentDao = studentDao;
        this.courseDao = courseDao;
    }
    
    @Override
    public void invoke(ScoreExcelDTO data, AnalysisContext context) {
        int rowNum = context.readRowHolder().getRowIndex() + 1;
        
        // 校验必填字段
        if (data.getStudentNo() == null || data.getStudentNo().trim().isEmpty()) {
            errors.add(new ErrorInfo(rowNum, "学号不能为空"));
            return;
        }
        if (data.getCourseCode() == null || data.getCourseCode().trim().isEmpty()) {
            errors.add(new ErrorInfo(rowNum, "课程编码不能为空"));
            return;
        }
        if (data.getScore() == null) {
            errors.add(new ErrorInfo(rowNum, "成绩不能为空"));
            return;
        }
        
        // 校验成绩范围
        if (data.getScore() < 0 || data.getScore() > 100) {
            errors.add(new ErrorInfo(rowNum, "成绩必须在0-100之间"));
            return;
        }
        
        // 查找学生
        Student student = findStudentByNo(data.getStudentNo().trim());
        if (student == null) {
            errors.add(new ErrorInfo(rowNum, "学号不存在: " + data.getStudentNo()));
            return;
        }
        
        // 查找课程
        Course course = findCourseByCode(data.getCourseCode().trim());
        if (course == null) {
            errors.add(new ErrorInfo(rowNum, "课程编码不存在: " + data.getCourseCode()));
            return;
        }
        
        // 创建成绩对象
        Score score = new Score();
        score.setStudentId(student.getId());
        score.setCourseId(course.getId());
        score.setScore(data.getScore());
        score.setExamType(data.getExamType());
        score.setStatus(1);
        
        // 解析考试日期
        if (data.getExamDate() != null && !data.getExamDate().trim().isEmpty()) {
            try {
                score.setExamDate(LocalDate.parse(data.getExamDate().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } catch (Exception e) {
                try {
                    score.setExamDate(LocalDate.parse(data.getExamDate().trim(), DateTimeFormatter.ofPattern("yyyy/MM/dd")));
                } catch (Exception e2) {
                    errors.add(new ErrorInfo(rowNum, "日期格式错误，请使用 yyyy-MM-dd 格式"));
                    return;
                }
            }
        }
        
        score.setRemark(data.getRemark());
        successData.add(score);
    }
    
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("成绩导入解析完成，成功 {} 条，失败 {} 条", successData.size(), errors.size());
    }
    
    private Student findStudentByNo(String studentNo) {
        return studentDao.findAll().stream()
                .filter(s -> studentNo.equals(s.getStudentNo()))
                .findFirst()
                .orElse(null);
    }
    
    private Course findCourseByCode(String courseCode) {
        return courseDao.findAll().stream()
                .filter(c -> courseCode.equals(c.getCourseCode()))
                .findFirst()
                .orElse(null);
    }
    
    public List<Score> getSuccessData() {
        return successData;
    }
    
    public List<ErrorInfo> getErrors() {
        return errors;
    }
    
    public static class ErrorInfo {
        private int row;
        private String message;
        
        public ErrorInfo(int row, String message) {
            this.row = row;
            this.message = message;
        }
        
        public int getRow() { return row; }
        public String getMessage() { return message; }
    }
}
