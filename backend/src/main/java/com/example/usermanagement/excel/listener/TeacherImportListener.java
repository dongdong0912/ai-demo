package com.example.usermanagement.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.example.usermanagement.entity.Teacher;
import com.example.usermanagement.excel.dto.TeacherExcelDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class TeacherImportListener extends AnalysisEventListener<TeacherExcelDTO> {

    private List<Teacher> successData = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
    private int rowNum = 1;

    @Override
    public void invoke(TeacherExcelDTO dto, AnalysisContext context) {
        rowNum++;
        StringBuilder rowError = new StringBuilder();

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            rowError.append("姓名不能为空; ");
        }
        if (dto.getStatus() != null && dto.getStatus() != 0 && dto.getStatus() != 1) {
            rowError.append("状态值只能为0或1; ");
        }
        if (dto.getIdCard() != null && !dto.getIdCard().isEmpty()
                && dto.getIdCard().length() > 18) {
            rowError.append("身份证号长度不能超过18位; ");
        }

        if (rowError.length() > 0) {
            errors.add("第" + rowNum + "行: " + rowError.toString().trim());
        } else {
            Teacher teacher = new Teacher();
            teacher.setName(dto.getName().trim());
            teacher.setGender(dto.getGender());
            teacher.setSubject(dto.getSubject());
            teacher.setTitle(dto.getTitle());
            teacher.setIdCard(dto.getIdCard());
            teacher.setPhone(dto.getPhone());
            teacher.setEmail(dto.getEmail());
            teacher.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
            successData.add(teacher);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }
}
