package com.example.usermanagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 成绩趋势DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreTrendDTO {
    
    // 时间维度
    private String examType;          // 考试类型
    private String examDate;          // 考试日期
    private Integer year;            // 年份
    private Integer month;           // 月份
    private String period;            // 时间段(用于图表展示)
    
    // 统计值
    private Double averageScore;      // 平均分
    private Double maxScore;          // 最高分
    private Double minScore;          // 最低分
    private Long examCount;          // 考试人数
    
    // 筛选条件
    private String schoolYear;
    private String semester;
    private Long courseId;
    private Long studentId;
    private Long classId;
}
