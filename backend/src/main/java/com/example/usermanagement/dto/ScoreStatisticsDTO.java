package com.example.usermanagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 成绩统计分析DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreStatisticsDTO {
    
    // 基本统计
    private Double averageScore;      // 平均分
    private Double maxScore;           // 最高分
    private Double minScore;           // 最低分
    private Double totalScore;         // 总分
    private Long totalCount;           // 考试人数
    
    // 及格率统计
    private Double passRate;           // 及格率(%)
    private Long passCount;            // 及格人数
    private Long failCount;            // 不及格人数
    
    // 分段统计
    private Long excellentCount;       // 优秀人数(90+)
    private Long goodCount;            // 良好人数(80-89)
    private Long mediumCount;          // 中等人数(70-79)
    private Long passCountLevel;       // 及格人数(60-69)
    private Long failLevelCount;       // 不及格人数(<60)
    
    // 成绩分布
    private Double excellentRate;      // 优秀率
    private Double goodRate;           // 良好率
    private Double mediumRate;         // 中等率
    
    // 筛选条件
    private String schoolYear;
    private String semester;
    private Long courseId;
    private Long classId;
    private Long gradeId;
    private String examType;
    
    // 附加信息
    private String courseName;
    private String className;
    private String gradeName;
}
