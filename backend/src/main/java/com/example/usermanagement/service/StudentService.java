package com.example.usermanagement.service;

import com.example.usermanagement.entity.Student;
import com.example.usermanagement.dao.StudentDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudentService {

    private final StudentDao studentDao;

    public StudentService(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    public List<Student> getAllStudents() {
        log.debug("Service: 查询所有学生");
        return studentDao.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        log.debug("Service: 根据ID查询学生, id={}", id);
        return studentDao.findById(id);
    }

    public List<Student> searchStudents(String keyword) {
        log.debug("Service: 搜索学生, keyword={}", keyword);
        List<Student> allStudents = studentDao.findAll();
        if (keyword == null || keyword.trim().isEmpty()) {
            return allStudents;
        }
        String lowerKeyword = keyword.toLowerCase().trim();
        return allStudents.stream()
                .filter(s -> {
                    boolean matchName = s.getName() != null && s.getName().toLowerCase().contains(lowerKeyword);
                    boolean matchStudentNo = s.getStudentNo() != null && s.getStudentNo().contains(keyword.trim());
                    boolean matchPhone = s.getPhone() != null && s.getPhone().contains(keyword.trim());
                    return matchName || matchStudentNo || matchPhone;
                })
                .collect(Collectors.toList());
    }

    public List<Student> getStudentsByClassId(Long classId) {
        log.debug("Service: 根据班级查询学生, classId={}", classId);
        return studentDao.findByClassId(classId);
    }

    public Student createStudent(Student student) {
        log.debug("Service: 创建学生, name={}", student.getName());
        return studentDao.save(student);
    }

    public Student updateStudent(Long id, Student studentDetails) {
        log.debug("Service: 更新学生, id={}", id);
        Student student = studentDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("学生不存在"));

        student.setName(studentDetails.getName());
        student.setGender(studentDetails.getGender());
        student.setIdCard(studentDetails.getIdCard());
        student.setClassId(studentDetails.getClassId());
        student.setPhone(studentDetails.getPhone());
        student.setParentPhone(studentDetails.getParentPhone());
        student.setParentName(studentDetails.getParentName());
        student.setAddress(studentDetails.getAddress());
        student.setBirthDate(studentDetails.getBirthDate());
        student.setNation(studentDetails.getNation());
        student.setNativePlace(studentDetails.getNativePlace());
        student.setEnrollmentDate(studentDetails.getEnrollmentDate());
        student.setRemark(studentDetails.getRemark());
        student.setPhoto(studentDetails.getPhoto());
        student.setStatus(studentDetails.getStatus());

        return studentDao.save(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        log.debug("Service: 删除学生, id={}", id);
        if (!studentDao.existsById(id)) {
            log.warn("Service: 学生不存在, id={}", id);
            throw new IllegalArgumentException("学生不存在");
        }
        studentDao.deleteById(id);
    }

    public long countByClassId(Long classId) {
        return studentDao.countByClassId(classId);
    }

    public long countActiveByClassId(Long classId) {
        return studentDao.countByClassIdAndStatus(classId, 1);
    }
}
