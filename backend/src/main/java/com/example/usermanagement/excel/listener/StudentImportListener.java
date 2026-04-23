package com.example.usermanagement.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.example.usermanagement.entity.Student;
import com.example.usermanagement.excel.dto.StudentExcelDTO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class StudentImportListener extends AnalysisEventListener<StudentExcelDTO> {

    private List<Student> successData = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
    private int rowNum = 1;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void invoke(StudentExcelDTO dto, AnalysisContext context) {
        rowNum++;
        StringBuilder rowError = new StringBuilder();

        if (dto.getStudentNo() == null || dto.getStudentNo().trim().isEmpty()) {
            rowError.append("学号不能为空; ");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            rowError.append("姓名不能为空; ");
        }
        if (dto.getStatus() != null && (dto.getStatus() < 0 || dto.getStatus() > 3)) {
            rowError.append("状态值为0-3之间; ");
        }
        if (dto.getClassId() != null && dto.getClassId() <= 0) {
            rowError.append("班级ID必须大于0; ");
        }

        if (rowError.length() > 0) {
            errors.add("第" + rowNum + "行: " + rowError.toString().trim());
        } else {
            Student student = new Student();
            student.setStudentNo(dto.getStudentNo().trim());
            student.setName(dto.getName().trim());
            student.setGender(dto.getGender());
            student.setIdCard(dto.getIdCard());
            student.setClassId(dto.getClassId());
            student.setPhone(dto.getPhone());
            student.setParentPhone(dto.getParentPhone());
            student.setParentName(dto.getParentName());
            student.setNation(dto.getNation());
            student.setNativePlace(dto.getNativePlace());
            if (dto.getBirthDate() != null && !dto.getBirthDate().isEmpty()) {
                try { student.setBirthDate(LocalDate.parse(dto.getBirthDate(), DATE_FMT)); } catch (Exception ignored) {}
            }
            if (dto.getEnrollmentDate() != null && !dto.getEnrollmentDate().isEmpty()) {
                try { student.setEnrollmentDate(LocalDate.parse(dto.getEnrollmentDate(), DATE_FMT)); } catch (Exception ignored) {}
            }
            student.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
            successData.add(student);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }
}
