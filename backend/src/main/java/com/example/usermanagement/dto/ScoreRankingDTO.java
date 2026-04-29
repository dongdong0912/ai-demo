package com.example.usermanagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 成绩排名DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRankingDTO {
    
    private Long studentId;
    private String studentNo;
    private String studentName;
    private Long classId;
    private String className;
    private String gradeName;
    
    // 排名相关
    private Double score;
    private Long classRank;           // 班级排名
    private Long gradeRank;           // 年级排名
    private Long totalInClass;        // 班级总人数
    private Long totalInGrade;        // 年级总人数
    
    // 百分比
    private Double classPercentile;   // 班级百分位
    private Double gradePercentile;   // 年级百分位
    
    // 附加信息
    private String schoolYear;
    private String semester;
    private Long courseId;
    private String courseName;
    private String examType;
    private String examDate;
}
