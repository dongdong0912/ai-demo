package com.example.usermanagement.excel.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@HeadRowHeight(20)
public class CourseExcelDTO {

    @ExcelProperty(value = "课程名称", index = 0)
    private String courseName;

    @ExcelProperty(value = "课程编码", index = 1)
    private String courseCode;

    @ExcelProperty(value = "类型", index = 2)
    private String type;

    @ExcelProperty(value = "学分", index = 3)
    private Double credit;

    @ExcelProperty(value = "总课时", index = 4)
    private Integer totalHours;

    @ExcelProperty(value = "分类", index = 5)
    private String category;

    @ExcelProperty(value = "授课教师ID", index = 6)
    private Long teacherId;

    @ExcelProperty(value = "状态", index = 7)
    private Integer status;
}
