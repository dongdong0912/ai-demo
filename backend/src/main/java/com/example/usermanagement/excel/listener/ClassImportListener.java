package com.example.usermanagement.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.example.usermanagement.entity.GradeClass;
import com.example.usermanagement.excel.dto.ClassExcelDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ClassImportListener extends AnalysisEventListener<ClassExcelDTO> {

    private List<GradeClass> successData = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
    private int rowNum = 1;

    @Override
    public void invoke(ClassExcelDTO dto, AnalysisContext context) {
        rowNum++;
        StringBuilder rowError = new StringBuilder();

        if (dto.getClassName() == null || dto.getClassName().trim().isEmpty()) {
            rowError.append("班级名称不能为空; ");
        }
        if (dto.getGradeName() == null || dto.getGradeName().trim().isEmpty()) {
            rowError.append("年级不能为空; ");
        }
        if (dto.getMaxStudents() != null && dto.getMaxStudents() <= 0) {
            rowError.append("最大人数必须大于0; ");
        }
        if (dto.getStatus() != null && dto.getStatus() != 0 && dto.getStatus() != 1) {
            rowError.append("状态值只能为0或1; ");
        }

        if (rowError.length() > 0) {
            errors.add("第" + rowNum + "行: " + rowError.toString().trim());
        } else {
            GradeClass gradeClass = new GradeClass();
            gradeClass.setClassName(dto.getClassName().trim());
            gradeClass.setGradeName(dto.getGradeName().trim());
            gradeClass.setHeadTeacherId(dto.getHeadTeacherId());
            gradeClass.setMaxStudents(dto.getMaxStudents() == null ? 50 : dto.getMaxStudents());
            gradeClass.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
            successData.add(gradeClass);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }
}
