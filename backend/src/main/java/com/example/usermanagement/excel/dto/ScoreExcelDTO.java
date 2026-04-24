package com.example.usermanagement.excel.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ScoreExcelDTO {
    
    @ExcelProperty("学号")
    private String studentNo;
    
    @ExcelProperty("学生姓名")
    private String studentName;
    
    @ExcelProperty("课程编码")
    private String courseCode;
    
    @ExcelProperty("课程名称")
    private String courseName;
    
    @ExcelProperty("成绩")
    private Double score;
    
    @ExcelProperty("考试类型")
    private String examType;
    
    @ExcelProperty("考试日期")
    private String examDate;
    
    @ExcelProperty("备注")
    private String remark;
}
