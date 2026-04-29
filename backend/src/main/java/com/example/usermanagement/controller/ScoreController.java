package com.example.usermanagement.controller;

import com.example.usermanagement.entity.Score;
import com.example.usermanagement.service.ScoreService;
import com.example.usermanagement.dto.ScoreStatisticsDTO;
import com.example.usermanagement.dto.ScoreRankingDTO;
import com.example.usermanagement.dto.ScoreTrendDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = "*")
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllScores(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long studentId) {
        log.debug("获取成绩列表, keyword={}, courseId={}, studentId={}", keyword, courseId, studentId);
        List<ScoreService.ScoreVO> scores = scoreService.getScoresWithDetails(keyword, courseId, studentId);
        log.info("成功获取成绩列表, 共 {} 条数据", scores.size());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", scores);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getScoreById(@PathVariable Long id) {
        log.debug("根据ID获取成绩: {}", id);
        return scoreService.getScoreById(id)
                .map(score -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", 200);
                    response.put("message", "success");
                    response.put("data", score);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    log.warn("成绩不存在, ID: {}", id);
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", 404);
                    response.put("message", "成绩不存在");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<Map<String, Object>> getScoresByStudentId(@PathVariable Long studentId) {
        log.debug("根据学生获取成绩, studentId={}", studentId);
        List<ScoreService.ScoreVO> scores = scoreService.getScoresWithDetails(null, null, studentId);
        log.info("成功获取学生成绩列表, 共 {} 条数据", scores.size());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", scores);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getScoresByCourseId(@PathVariable Long courseId) {
        log.debug("根据课程获取成绩, courseId={}", courseId);
        List<ScoreService.ScoreVO> scores = scoreService.getScoresWithDetails(null, courseId, null);
        log.info("成功获取课程成绩列表, 共 {} 条数据", scores.size());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", scores);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createScore(@RequestBody Score score) {
        log.info("创建成绩: studentId={}, courseId={}", score.getStudentId(), score.getCourseId());
        try {
            Score createdScore = scoreService.createScore(score);
            log.info("成绩创建成功, ID: {}", createdScore.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 201);
            response.put("message", "创建成功");
            response.put("data", createdScore);
            return ResponseEntity.status(201).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("创建成绩失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateScore(@PathVariable Long id, @RequestBody Score score) {
        log.info("更新成绩, ID: {}", id);
        try {
            Score updatedScore = scoreService.updateScore(id, score);
            log.info("成绩更新成功, ID: {}", id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", updatedScore);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("更新成绩失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", e.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteScore(@PathVariable Long id) {
        log.info("删除成绩, ID: {}", id);
        try {
            scoreService.deleteScore(id);
            log.info("成绩删除成功, ID: {}", id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("删除成绩失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", e.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }

    // ==================== 统计分析 API ====================

    /**
     * 获取成绩统计分析
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics(
            @RequestParam(required = false) String schoolYear,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long gradeId,
            @RequestParam(required = false) String examType) {
        log.info("统计分析请求: schoolYear={}, semester={}, courseId={}, classId={}, examType={}", 
                 schoolYear, semester, courseId, classId, examType);
        try {
            ScoreStatisticsDTO statistics = scoreService.getStatistics(
                    schoolYear, semester, courseId, classId, gradeId, examType);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", statistics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("统计分析失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "统计分析失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取成绩排名
     */
    @GetMapping("/ranking")
    public ResponseEntity<Map<String, Object>> getRanking(
            @RequestParam(required = false) String schoolYear,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long gradeId,
            @RequestParam(required = false) String examType,
            @RequestParam(required = false, defaultValue = "desc") String orderBy) {
        log.info("成绩排名请求: schoolYear={}, semester={}, courseId={}, classId={}, orderBy={}", 
                 schoolYear, semester, courseId, classId, orderBy);
        try {
            List<ScoreRankingDTO> rankings = scoreService.getRanking(
                    schoolYear, semester, courseId, classId, gradeId, examType, orderBy);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", rankings);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("成绩排名失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "成绩排名失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取成绩趋势
     */
    @GetMapping("/trend")
    public ResponseEntity<Map<String, Object>> getTrend(
            @RequestParam(required = false) String schoolYear,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long classId) {
        log.info("成绩趋势请求: schoolYear={}, semester={}, courseId={}, studentId={}", 
                 schoolYear, semester, courseId, studentId);
        try {
            List<ScoreTrendDTO> trends = scoreService.getTrend(
                    schoolYear, semester, courseId, studentId, classId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", trends);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("成绩趋势获取失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "成绩趋势获取失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 多维度成绩查询
     */
    @GetMapping("/query")
    public ResponseEntity<Map<String, Object>> queryScores(
            @RequestParam(required = false) String schoolYear,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String examType,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("多维度查询请求: schoolYear={}, semester={}, courseId={}, studentId={}", 
                 schoolYear, semester, courseId, studentId);
        try {
            List<ScoreService.ScoreVO> scores = scoreService.queryScores(
                    schoolYear, semester, courseId, studentId, classId, keyword, examType, page, size);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", scores);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("多维度查询失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "多维度查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取学生个人成绩单
     */
    @GetMapping("/report/{studentId}")
    public ResponseEntity<Map<String, Object>> getStudentReport(
            @PathVariable Long studentId,
            @RequestParam(required = false) String schoolYear,
            @RequestParam(required = false) String semester) {
        log.info("学生成绩单请求: studentId={}, schoolYear={}, semester={}", studentId, schoolYear, semester);
        try {
            Map<String, Object> report = scoreService.getStudentReport(studentId, schoolYear, semester);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", report);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取成绩单失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", e.getMessage());
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            log.error("获取成绩单失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取成绩单失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
