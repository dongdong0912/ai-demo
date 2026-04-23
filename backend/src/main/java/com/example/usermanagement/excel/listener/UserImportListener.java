package com.example.usermanagement.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.excel.dto.UserExcelDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class UserImportListener extends AnalysisEventListener<UserExcelDTO> {

    private List<User> successData = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
    private int rowNum = 1;

    @Override
    public void invoke(UserExcelDTO dto, AnalysisContext context) {
        rowNum++;
        StringBuilder rowError = new StringBuilder();

        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            rowError.append("用户名不能空; ");
        }
        if (dto.getStatus() != null && dto.getStatus() != 0 && dto.getStatus() != 1) {
            rowError.append("状态值只能为0或1; ");
        }
        if (dto.getRole() != null && !dto.getRole().matches("(?i)ADMIN|USER")) {
            rowError.append("角色只能是ADMIN或USER; ");
        }
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()
                && !dto.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            rowError.append("邮箱格式不正确; ");
        }

        if (rowError.length() > 0) {
            errors.add("第" + rowNum + "行: " + rowError.toString().trim());
        } else {
            User user = new User();
            user.setUsername(dto.getUsername().trim());
            user.setRealName(dto.getRealName());
            user.setGender(dto.getGender());
            user.setRole(dto.getRole() == null ? "USER" : dto.getRole().toUpperCase());
            user.setEmail(dto.getEmail());
            user.setPhone(dto.getPhone());
            user.setDepartment(dto.getDepartment());
            user.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
            successData.add(user);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }
}
