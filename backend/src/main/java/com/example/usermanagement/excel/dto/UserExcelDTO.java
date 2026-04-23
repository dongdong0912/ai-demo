package com.example.usermanagement.excel.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@HeadRowHeight(20)
public class UserExcelDTO {

    @ExcelProperty(value = "用户名", index = 0)
    private String username;

    @ExcelProperty(value = "真实姓名", index = 1)
    private String realName;

    @ExcelProperty(value = "性别", index = 2)
    private String gender;

    @ExcelProperty(value = "角色", index = 3)
    private String role;

    @ExcelProperty(value = "邮箱", index = 4)
    private String email;

    @ExcelProperty(value = "手机号", index = 5)
    private String phone;

    @ExcelProperty(value = "部门", index = 6)
    private String department;

    @ExcelProperty(value = "状态", index = 7)
    private Integer status;
}
