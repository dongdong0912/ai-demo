package com.example.usermanagement.excel.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@HeadRowHeight(20)
public class StudentExcelDTO {

    @ExcelProperty(value = "学号", index = 0)
    private String studentNo;

    @ExcelProperty(value = "姓名", index = 1)
    private String name;

    @ExcelProperty(value = "性别", index = 2)
    private String gender;

    @ExcelProperty(value = "身份证号", index = 3)
    private String idCard;

    @ExcelProperty(value = "班级ID", index = 4)
    private Long classId;

    @ExcelProperty(value = "手机号", index = 5)
    private String phone;

    @ExcelProperty(value = "家长电话", index = 6)
    private String parentPhone;

    @ExcelProperty(value = "家长姓名", index = 7)
    private String parentName;

    @ExcelProperty(value = "民族", index = 8)
    private String nation;

    @ExcelProperty(value = "籍贯", index = 9)
    private String nativePlace;

    @ExcelProperty(value = "出生日期", index = 10)
    private String birthDate;

    @ExcelProperty(value = "入学日期", index = 11)
    private String enrollmentDate;

    @ExcelProperty(value = "状态", index = 12)
    private Integer status;
}
