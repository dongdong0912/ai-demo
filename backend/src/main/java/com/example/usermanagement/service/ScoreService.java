package com.example.usermanagement.service;

import com.example.usermanagement.entity.Score;
import com.example.usermanagement.dao.ScoreDao;
import com.example.usermanagement.dao.StudentDao;
import com.example.usermanagement.dao.CourseDao;
import com.example.usermanagement.entity.Student;
import com.example.usermanagement.entity.Course;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScoreService {

    private final ScoreDao scoreDao;
    private final StudentDao studentDao;
    private final CourseDao courseDao;

    public ScoreService(ScoreDao scoreDao, StudentDao studentDao, CourseDao courseDao) {
        this.scoreDao = scoreDao;
        this.studentDao = studentDao;
        this.courseDao = courseDao;
    }

    public List<Score> getAllScores() {
        log.debug("Service: 查询所有成绩");
        return scoreDao.findAll();
    }

    public Optional<Score> getScoreById(Long id) {
        log.debug("Service: 根据ID查询成绩, id={}", id);
        return scoreDao.findById(id);
    }

    public List<Score> searchScores(String keyword, Long courseId, Long studentId) {
        log.debug("Service: 搜索成绩, keyword={}, courseId={}, studentId={}", keyword, courseId, studentId);
        List<Score> allScores = scoreDao.findAll();
        
        return allScores.stream()
                .filter(s -> {
                    // 按课程筛选
                    if (courseId != null && !courseId.equals(s.getCourseId())) {
                        return false;
                    }
                    // 按学生筛选
                    if (studentId != null && !studentId.equals(s.getStudentId())) {
                        return false;
                    }
                    // 按关键字筛选（学生姓名或学号）
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        String lowerKeyword = keyword.toLowerCase().trim();
                        Optional<Student> studentOpt = studentDao.findById(s.getStudentId());
                        if (studentOpt.isPresent()) {
                            Student student = studentOpt.get();
                            boolean matchName = student.getName() != null && student.getName().toLowerCase().contains(lowerKeyword);
                            boolean matchStudentNo = student.getStudentNo() != null && student.getStudentNo().contains(keyword.trim());
                            if (!matchName && !matchStudentNo) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public List<Score> getScoresByStudentId(Long studentId) {
        log.debug("Service: 根据学生查询成绩, studentId={}", studentId);
        return scoreDao.findByStudentId(studentId);
    }

    public List<Score> getScoresByCourseId(Long courseId) {
        log.debug("Service: 根据课程查询成绩, courseId={}", courseId);
        return scoreDao.findByCourseId(courseId);
    }

    public Score createScore(Score score) {
        log.debug("Service: 创建成绩, studentId={}, courseId={}", score.getStudentId(), score.getCourseId());
        // 验证学生存在
        if (!studentDao.existsById(score.getStudentId())) {
            throw new IllegalArgumentException("学生不存在");
        }
        // 验证课程存在
        if (!courseDao.existsById(score.getCourseId())) {
            throw new IllegalArgumentException("课程不存在");
        }
        return scoreDao.save(score);
    }

    public Score updateScore(Long id, Score scoreDetails) {
        log.debug("Service: 更新成绩, id={}", id);
        Score score = scoreDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("成绩不存在"));
        
        score.setScore(scoreDetails.getScore());
        score.setExamType(scoreDetails.getExamType());
        score.setExamDate(scoreDetails.getExamDate());
        score.setRemark(scoreDetails.getRemark());
        score.setStatus(scoreDetails.getStatus());
        
        return scoreDao.save(score);
    }

    @Transactional
    public void deleteScore(Long id) {
        log.debug("Service: 删除成绩, id={}", id);
        if (!scoreDao.existsById(id)) {
            log.warn("Service: 成绩不存在, id={}", id);
            throw new IllegalArgumentException("成绩不存在");
        }
        scoreDao.deleteById(id);
    }

    @Transactional
    public void deleteScoresByStudentId(Long studentId) {
        log.debug("Service: 删除学生所有成绩, studentId={}", studentId);
        scoreDao.deleteByStudentId(studentId);
    }

    @Transactional
    public void deleteScoresByCourseId(Long courseId) {
        log.debug("Service: 删除课程所有成绩, courseId={}", courseId);
        scoreDao.deleteByCourseId(courseId);
    }

    // 获取带学生和课程信息的成绩列表
    public List<ScoreVO> getScoresWithDetails(String keyword, Long courseId, Long studentId) {
        List<Score> scores = searchScores(keyword, courseId, studentId);
        return scores.stream().map(this::toScoreVO).collect(Collectors.toList());
    }

    private ScoreVO toScoreVO(Score score) {
        ScoreVO vo = new ScoreVO();
        vo.setId(score.getId());
        vo.setStudentId(score.getStudentId());
        vo.setCourseId(score.getCourseId());
        vo.setScore(score.getScore());
        vo.setExamType(score.getExamType());
        vo.setExamDate(score.getExamDate());
        vo.setRemark(score.getRemark());
        vo.setStatus(score.getStatus());
        vo.setCreateTime(score.getCreateTime());
        vo.setUpdateTime(score.getUpdateTime());
        
        // 填充学生信息
        studentDao.findById(score.getStudentId()).ifPresent(student -> {
            vo.setStudentName(student.getName());
            vo.setStudentNo(student.getStudentNo());
            vo.setClassId(student.getClassId());
        });
        
        // 填充课程信息
        courseDao.findById(score.getCourseId()).ifPresent(course -> {
            vo.setCourseName(course.getCourseName());
            vo.setCourseCode(course.getCourseCode());
        });
        
        return vo;
    }

    // 成绩VO内部类
    @Data
    public static class ScoreVO {
        private Long id;
        private Long studentId;
        private Long courseId;
        private Double score;
        private String examType;
        private java.time.LocalDate examDate;
        private String remark;
        private Integer status;
        private java.time.LocalDateTime createTime;
        private java.time.LocalDateTime updateTime;
        
        // 学生信息
        private String studentName;
        private String studentNo;
        private Long classId;
        
        // 课程信息
        private String courseName;
        private String courseCode;
    }
}
