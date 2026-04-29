package com.example.usermanagement.service;

import com.example.usermanagement.entity.Score;
import com.example.usermanagement.dao.ScoreDao;
import com.example.usermanagement.dao.StudentDao;
import com.example.usermanagement.dao.CourseDao;
import com.example.usermanagement.dao.GradeClassDao;
import com.example.usermanagement.entity.Student;
import com.example.usermanagement.entity.Course;
import com.example.usermanagement.entity.GradeClass;
import com.example.usermanagement.dto.ScoreStatisticsDTO;
import com.example.usermanagement.dto.ScoreRankingDTO;
import com.example.usermanagement.dto.ScoreTrendDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScoreService {

    private final ScoreDao scoreDao;
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final GradeClassDao gradeClassDao;

    public ScoreService(ScoreDao scoreDao, StudentDao studentDao, CourseDao courseDao, GradeClassDao gradeClassDao) {
        this.scoreDao = scoreDao;
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.gradeClassDao = gradeClassDao;
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
        
        // 学期学年信息
        private String schoolYear;
        private String semester;
    }

    // ==================== 统计分析功能 ====================
    
    /**
     * 获取成绩统计分析
     */
    public ScoreStatisticsDTO getStatistics(String schoolYear, String semester, Long courseId, 
                                            Long classId, Long gradeId, String examType) {
        log.debug("统计分析: schoolYear={}, semester={}, courseId={}, classId={}, examType={}", 
                  schoolYear, semester, courseId, classId, examType);
        
        List<Score> scores = filterScores(schoolYear, semester, courseId, classId, gradeId, examType);
        
        if (scores.isEmpty()) {
            ScoreStatisticsDTO emptyStat = new ScoreStatisticsDTO();
            emptyStat.setSchoolYear(schoolYear);
            emptyStat.setSemester(semester);
            emptyStat.setCourseId(courseId);
            emptyStat.setClassId(classId);
            emptyStat.setExamType(examType);
            return emptyStat;
        }
        
        ScoreStatisticsDTO stats = new ScoreStatisticsDTO();
        stats.setSchoolYear(schoolYear);
        stats.setSemester(semester);
        stats.setCourseId(courseId);
        stats.setClassId(classId);
        stats.setGradeId(gradeId);
        stats.setExamType(examType);
        stats.setTotalCount((long) scores.size());
        
        // 计算分数统计
        DoubleSummaryStatistics summary = scores.stream()
                .mapToDouble(Score::getScore)
                .summaryStatistics();
        
        stats.setAverageScore(Math.round(summary.getAverage() * 100.0) / 100.0);
        stats.setMaxScore(summary.getMax());
        stats.setMinScore(summary.getMin());
        stats.setTotalScore(summary.getSum());
        
        // 计算及格率 (60分为及格线)
        long passCount = scores.stream().filter(s -> s.getScore() >= 60).count();
        long failCount = scores.size() - passCount;
        stats.setPassCount(passCount);
        stats.setFailCount(failCount);
        stats.setPassRate(Math.round((double) passCount / scores.size() * 10000.0) / 100.0);
        
        // 分段统计
        stats.setExcellentCount((long) scores.stream().filter(s -> s.getScore() >= 90).count());
        stats.setGoodCount((long) scores.stream().filter(s -> s.getScore() >= 80 && s.getScore() < 90).count());
        stats.setMediumCount((long) scores.stream().filter(s -> s.getScore() >= 70 && s.getScore() < 80).count());
        stats.setPassCountLevel((long) scores.stream().filter(s -> s.getScore() >= 60 && s.getScore() < 70).count());
        stats.setFailLevelCount(failCount);
        
        // 计算各等级比例
        stats.setExcellentRate(Math.round((double) stats.getExcellentCount() / scores.size() * 10000.0) / 100.0);
        stats.setGoodRate(Math.round((double) stats.getGoodCount() / scores.size() * 10000.0) / 100.0);
        stats.setMediumRate(Math.round((double) stats.getMediumCount() / scores.size() * 10000.0) / 100.0);
        
        // 填充附加信息
        if (courseId != null) {
            courseDao.findById(courseId).ifPresent(c -> stats.setCourseName(c.getCourseName()));
        }
        if (classId != null) {
            gradeClassDao.findById(classId).ifPresent(c -> {
                stats.setClassName(c.getClassName());
                stats.setGradeName(c.getGradeName());
            });
        }
        
        return stats;
    }
    
    /**
     * 获取成绩排名列表
     */
    public List<ScoreRankingDTO> getRanking(String schoolYear, String semester, Long courseId,
                                            Long classId, Long gradeId, String examType, 
                                            String orderBy) {
        log.debug("成绩排名: schoolYear={}, semester={}, courseId={}, classId={}, orderBy={}", 
                  schoolYear, semester, courseId, classId, orderBy);
        
        List<Score> scores = filterScores(schoolYear, semester, courseId, classId, gradeId, examType);
        
        // 构建排名DTO
        List<ScoreRankingDTO> rankings = scores.stream().map(score -> {
            ScoreRankingDTO dto = new ScoreRankingDTO();
            dto.setScore(score.getScore());
            dto.setExamType(score.getExamType());
            dto.setExamDate(score.getExamDate() != null ? score.getExamDate().toString() : null);
            dto.setSchoolYear(score.getSchoolYear());
            dto.setSemester(score.getSemester());
            dto.setCourseId(score.getCourseId());
            
            // 学生信息
            studentDao.findById(score.getStudentId()).ifPresent(student -> {
                dto.setStudentId(student.getId());
                dto.setStudentNo(student.getStudentNo());
                dto.setStudentName(student.getName());
                dto.setClassId(student.getClassId());
                
                if (student.getClassId() != null) {
                    gradeClassDao.findById(student.getClassId()).ifPresent(cls -> {
                        dto.setClassName(cls.getClassName());
                        dto.setGradeName(cls.getGradeName());
                    });
                }
            });
            
            // 课程信息
            courseDao.findById(score.getCourseId()).ifPresent(course -> {
                dto.setCourseName(course.getCourseName());
            });
            
            return dto;
        }).collect(Collectors.toList());
        
        // 排序
        if ("asc".equalsIgnoreCase(orderBy)) {
            rankings.sort(Comparator.comparing(ScoreRankingDTO::getScore));
        } else {
            rankings.sort(Comparator.comparing(ScoreRankingDTO::getScore).reversed());
        }
        
        // 计算排名
        calculateRanks(rankings, classId, gradeId);
        
        return rankings;
    }
    
    /**
     * 获取成绩趋势数据
     */
    public List<ScoreTrendDTO> getTrend(String schoolYear, String semester, Long courseId,
                                        Long studentId, Long classId) {
        log.debug("成绩趋势: schoolYear={}, semester={}, courseId={}, studentId={}", 
                  schoolYear, semester, courseId, studentId);
        
        List<Score> scores = filterScores(schoolYear, semester, courseId, classId, null, null);
        
        // 按考试类型分组统计
        return scores.stream()
                .collect(Collectors.groupingBy(s -> s.getExamType() + "_" + 
                        (s.getExamDate() != null ? s.getExamDate().toString() : "未知")))
                .entrySet().stream()
                .map(entry -> {
                    String[] parts = entry.getKey().split("_", 2);
                    List<Score> groupScores = entry.getValue();
                    
                    ScoreTrendDTO trend = new ScoreTrendDTO();
                    trend.setExamType(parts[0]);
                    trend.setExamDate(parts.length > 1 ? parts[1] : null);
                    trend.setSchoolYear(schoolYear);
                    trend.setSemester(semester);
                    trend.setCourseId(courseId);
                    trend.setStudentId(studentId);
                    trend.setClassId(classId);
                    
                    if (entry.getValue().get(0).getExamDate() != null) {
                        trend.setYear(entry.getValue().get(0).getExamDate().getYear());
                        trend.setMonth(entry.getValue().get(0).getExamDate().getMonthValue());
                    }
                    
                    DoubleSummaryStatistics summary = groupScores.stream()
                            .mapToDouble(Score::getScore)
                            .summaryStatistics();
                    
                    trend.setAverageScore(Math.round(summary.getAverage() * 100.0) / 100.0);
                    trend.setMaxScore(summary.getMax());
                    trend.setMinScore(summary.getMin());
                    trend.setExamCount((long) groupScores.size());
                    
                    return trend;
                })
                .sorted(Comparator.comparing(ScoreTrendDTO::getExamDate))
                .collect(Collectors.toList());
    }
    
    /**
     * 多维度成绩查询
     */
    public List<ScoreVO> queryScores(String schoolYear, String semester, Long courseId,
                                     Long studentId, Long classId, String keyword,
                                     String examType, Integer page, Integer size) {
        log.debug("多维度查询: schoolYear={}, semester={}, courseId={}, studentId={}, classId={}", 
                  schoolYear, semester, courseId, studentId, classId);
        
        List<Score> scores = filterScores(schoolYear, semester, courseId, classId, null, examType);
        
        // 进一步筛选
        List<Score> filtered = scores.stream()
                .filter(s -> studentId == null || s.getStudentId().equals(studentId))
                .filter(s -> {
                    if (keyword == null || keyword.trim().isEmpty()) return true;
                    String lowerKeyword = keyword.toLowerCase().trim();
                    Optional<Student> studentOpt = studentDao.findById(s.getStudentId());
                    if (studentOpt.isPresent()) {
                        Student student = studentOpt.get();
                        boolean matchName = student.getName() != null && 
                                           student.getName().toLowerCase().contains(lowerKeyword);
                        boolean matchNo = student.getStudentNo() != null && 
                                         student.getStudentNo().contains(keyword.trim());
                        return matchName || matchNo;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        
        // 分页
        if (page != null && size != null) {
            int start = (page - 1) * size;
            int end = Math.min(start + size, filtered.size());
            if (start >= filtered.size()) {
                return new ArrayList<>();
            }
            return filtered.subList(start, end).stream()
                    .map(this::toScoreVO)
                    .collect(Collectors.toList());
        }
        
        return filtered.stream().map(this::toScoreVO).collect(Collectors.toList());
    }
    
    /**
     * 获取学生个人成绩单
     */
    public Map<String, Object> getStudentReport(Long studentId, String schoolYear, String semester) {
        log.debug("学生成绩单: studentId={}, schoolYear={}, semester={}", studentId, schoolYear, semester);
        
        Map<String, Object> report = new HashMap<>();
        
        // 获取学生信息
        Student student = studentDao.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("学生不存在"));
        report.put("studentId", student.getId());
        report.put("studentNo", student.getStudentNo());
        report.put("studentName", student.getName());
        
        if (student.getClassId() != null) {
            gradeClassDao.findById(student.getClassId()).ifPresent(cls -> {
                report.put("className", cls.getClassName());
                report.put("gradeName", cls.getGradeName());
            });
        }
        
        // 筛选成绩
        List<Score> scores = filterScores(schoolYear, semester, null, null, null, null);
        scores = scores.stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .collect(Collectors.toList());
        
        // 成绩列表
        List<ScoreVO> scoreList = scores.stream().map(this::toScoreVO).collect(Collectors.toList());
        report.put("scores", scoreList);
        
        // 总体统计
        if (!scores.isEmpty()) {
            DoubleSummaryStatistics summary = scores.stream()
                    .mapToDouble(Score::getScore)
                    .summaryStatistics();
            
            Map<String, Object> overallStats = new HashMap<>();
            overallStats.put("totalExams", scores.size());
            overallStats.put("averageScore", Math.round(summary.getAverage() * 100.0) / 100.0);
            overallStats.put("maxScore", summary.getMax());
            overallStats.put("minScore", summary.getMin());
            overallStats.put("totalScore", summary.getSum());
            overallStats.put("passRate", Math.round((double) scores.stream().filter(s -> s.getScore() >= 60).count() / scores.size() * 10000.0) / 100.0);
            report.put("overallStats", overallStats);
            
            // 各科统计
            List<Map<String, Object>> courseStats = scores.stream()
                    .collect(Collectors.groupingBy(s -> s.getCourseId().toString()))
                    .entrySet().stream()
                    .map(entry -> {
                        Long courseId = Long.parseLong(entry.getKey());
                        List<Score> courseScores = entry.getValue();
                        Map<String, Object> courseStat = new HashMap<>();
                        courseStat.put("courseId", courseId);
                        courseDao.findById(courseId).ifPresent(c -> courseStat.put("courseName", c.getCourseName()));
                        
                        DoubleSummaryStatistics cs = courseScores.stream()
                                .mapToDouble(Score::getScore)
                                .summaryStatistics();
                        courseStat.put("averageScore", Math.round(cs.getAverage() * 100.0) / 100.0);
                        courseStat.put("maxScore", cs.getMax());
                        courseStat.put("minScore", cs.getMin());
                        courseStat.put("examCount", courseScores.size());
                        return courseStat;
                    })
                    .collect(Collectors.toList());
            report.put("courseStats", courseStats);
        }
        
        report.put("schoolYear", schoolYear);
        report.put("semester", semester);
        
        return report;
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 通用成绩筛选
     */
    private List<Score> filterScores(String schoolYear, String semester, Long courseId,
                                    Long classId, Long gradeId, String examType) {
        List<Score> scores = scoreDao.findAll();
        
        return scores.stream()
                .filter(s -> schoolYear == null || schoolYear.isEmpty() || 
                           (s.getSchoolYear() != null && s.getSchoolYear().equals(schoolYear)))
                .filter(s -> semester == null || semester.isEmpty() || 
                           (s.getSemester() != null && s.getSemester().equals(semester)))
                .filter(s -> courseId == null || s.getCourseId().equals(courseId))
                .filter(s -> examType == null || examType.isEmpty() || 
                           (s.getExamType() != null && s.getExamType().equals(examType)))
                .filter(s -> {
                    if (classId == null && gradeId == null) return true;
                    Optional<Student> studentOpt = studentDao.findById(s.getStudentId());
                    if (!studentOpt.isPresent()) return false;
                    Student student = studentOpt.get();
                    
                    if (classId != null && classId.equals(student.getClassId())) {
                        return true;
                    }
                    if (gradeId != null && student.getClassId() != null) {
                        Optional<GradeClass> classOpt = gradeClassDao.findById(student.getClassId());
                        if (classOpt.isPresent() && gradeId.equals(classOpt.get().getId())) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 计算排名
     */
    private void calculateRanks(List<ScoreRankingDTO> rankings, Long classId, Long gradeId) {
        // 计算班级排名
        if (classId != null) {
            List<ScoreRankingDTO> classRankings = rankings.stream()
                    .filter(r -> classId.equals(r.getClassId()))
                    .collect(Collectors.toList());
            long totalInClass = classRankings.size();
            for (int i = 0; i < classRankings.size(); i++) {
                classRankings.get(i).setClassRank((long) i + 1);
                classRankings.get(i).setTotalInClass(totalInClass);
                classRankings.get(i).setClassPercentile(
                        Math.round((double) (totalInClass - i) / totalInClass * 10000.0) / 100.0);
            }
        }
        
        // 计算年级排名
        if (gradeId != null) {
            List<ScoreRankingDTO> gradeRankings = rankings.stream()
                    .filter(r -> gradeId != null && r.getGradeName() != null)
                    .collect(Collectors.toList());
            long totalInGrade = gradeRankings.size();
            for (int i = 0; i < gradeRankings.size(); i++) {
                gradeRankings.get(i).setGradeRank((long) i + 1);
                gradeRankings.get(i).setTotalInGrade(totalInGrade);
                gradeRankings.get(i).setGradePercentile(
                        Math.round((double) (totalInGrade - i) / totalInGrade * 10000.0) / 100.0);
            }
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
