package com.example.usermanagement.excel.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@HeadRowHeight(20)
public class TeacherExcelDTO {

    @ExcelProperty(value = "姓名", index = 0)
    private String name;

    @ExcelProperty(value = "性别", index = 1)
    private String gender;

    @ExcelProperty(value = "科目", index = 2)
    private String subject;

    @ExcelProperty(value = "职称", index = 3)
    private String title;

    @ExcelProperty(value = "身份证号", index = 4)
    private String idCard;

    @ExcelProperty(value = "手机号", index = 5)
    private String phone;

    @ExcelProperty(value = "邮箱", index = 6)
    private String email;

    @ExcelProperty(value = "状态", index = 7)
    private Integer status;
}
