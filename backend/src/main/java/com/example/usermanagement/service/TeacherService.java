package com.example.usermanagement.service;

import com.example.usermanagement.entity.Teacher;
import com.example.usermanagement.dao.TeacherDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TeacherService {

    private final TeacherDao teacherDao;

    public TeacherService(TeacherDao teacherDao) {
        this.teacherDao = teacherDao;
    }

    public List<Teacher> getAllTeachers() {
        log.debug("Service: 查询所有老师");
        return teacherDao.findAll();
    }

    public Optional<Teacher> getTeacherById(Long id) {
        log.debug("Service: 根据ID查询老师, id={}", id);
        return teacherDao.findById(id);
    }

    public List<Teacher> searchTeachers(String keyword) {
        log.debug("Service: 搜索老师, keyword={}", keyword);
        List<Teacher> allTeachers = teacherDao.findAll();
        if (keyword == null || keyword.trim().isEmpty()) {
            return allTeachers;
        }
        String lowerKeyword = keyword.toLowerCase().trim();
        return allTeachers.stream()
                .filter(t -> t.getName() != null && t.getName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public Teacher createTeacher(Teacher teacher) {
        log.debug("Service: 创建老师, name={}", teacher.getName());
        return teacherDao.save(teacher);
    }

    public Teacher updateTeacher(Long id, Teacher teacherDetails) {
        log.debug("Service: 更新老师, id={}", id);
        Teacher teacher = teacherDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("老师不存在"));

        teacher.setName(teacherDetails.getName());
        teacher.setGender(teacherDetails.getGender());
        teacher.setSubject(teacherDetails.getSubject());
        teacher.setPhone(teacherDetails.getPhone());
        teacher.setEmail(teacherDetails.getEmail());
        teacher.setRemark(teacherDetails.getRemark());
        teacher.setStatus(teacherDetails.getStatus());

        return teacherDao.save(teacher);
    }

    @Transactional
    public void deleteTeacher(Long id) {
        log.debug("Service: 删除老师, id={}", id);
        if (!teacherDao.existsById(id)) {
            log.warn("Service: 老师不存在, id={}", id);
            throw new IllegalArgumentException("老师不存在");
        }
        teacherDao.deleteById(id);
    }
}
