package com.example.usermanagement.excel.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@HeadRowHeight(20)
public class ClassExcelDTO {

    @ExcelProperty(value = "班级名称", index = 0)
    private String className;

    @ExcelProperty(value = "年级", index = 1)
    private String gradeName;

    @ExcelProperty(value = "班主任ID", index = 2)
    private Long headTeacherId;

    @ExcelProperty(value = "最大人数", index = 3)
    private Integer maxStudents;

    @ExcelProperty(value = "状态", index = 4)
    private Integer status;
}
