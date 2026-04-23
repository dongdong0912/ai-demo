package com.example.usermanagement.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.example.usermanagement.entity.Course;
import com.example.usermanagement.excel.dto.CourseExcelDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class CourseImportListener extends AnalysisEventListener<CourseExcelDTO> {

    private List<Course> successData = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
    private int rowNum = 1;

    @Override
    public void invoke(CourseExcelDTO dto, AnalysisContext context) {
        rowNum++;
        StringBuilder rowError = new StringBuilder();

        if (dto.getCourseName() == null || dto.getCourseName().trim().isEmpty()) {
            rowError.append("课程名称不能为空; ");
        }
        if (dto.getCourseCode() == null || dto.getCourseCode().trim().isEmpty()) {
            rowError.append("课程编码不能为空; ");
        }
        if (dto.getCredit() != null && dto.getCredit() < 0) {
            rowError.append("学分不能为负数; ");
        }
        if (dto.getTotalHours() != null && dto.getTotalHours() < 0) {
            rowError.append("总课时不能为负数; ");
        }
        if (dto.getStatus() != null && dto.getStatus() != 0 && dto.getStatus() != 1) {
            rowError.append("状态值只能为0或1; ");
        }

        if (rowError.length() > 0) {
            errors.add("第" + rowNum + "行: " + rowError.toString().trim());
        } else {
            Course course = new Course();
            course.setCourseName(dto.getCourseName().trim());
            course.setCourseCode(dto.getCourseCode().trim());
            course.setType(dto.getType());
            course.setCredit(dto.getCredit());
            course.setTotalHours(dto.getTotalHours());
            course.setCategory(dto.getCategory());
            course.setTeacherId(dto.getTeacherId());
            course.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
            successData.add(course);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }
}
