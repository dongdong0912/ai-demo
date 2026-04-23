package com.example.usermanagement.service;

import com.example.usermanagement.entity.GradeClass;
import com.example.usermanagement.dao.GradeClassDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class GradeClassService {

    private final GradeClassDao gradeClassDao;

    public GradeClassService(GradeClassDao gradeClassDao) {
        this.gradeClassDao = gradeClassDao;
    }

    public List<GradeClass> getAllClasses() {
        log.debug("Service: 查询所有班级");
        return gradeClassDao.findAll();
    }

    public Optional<GradeClass> getClassById(Long id) {
        log.debug("Service: 根据ID查询班级, id={}", id);
        return gradeClassDao.findById(id);
    }

    public GradeClass createClass(GradeClass gradeClass) {
        log.debug("Service: 创建班级, name={}", gradeClass.getClassName());
        if (gradeClass.getMaxStudents() == null) {
            gradeClass.setMaxStudents(50);
        }
        return gradeClassDao.save(gradeClass);
    }

    public GradeClass updateClass(Long id, GradeClass gradeClassDetails) {
        log.debug("Service: 更新班级, id={}", id);
        GradeClass gradeClass = gradeClassDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("班级不存在"));

        gradeClass.setClassName(gradeClassDetails.getClassName());
        gradeClass.setGradeName(gradeClassDetails.getGradeName());
        gradeClass.setHeadTeacherId(gradeClassDetails.getHeadTeacherId());
        gradeClass.setMaxStudents(gradeClassDetails.getMaxStudents());
        gradeClass.setStatus(gradeClassDetails.getStatus());

        return gradeClassDao.save(gradeClass);
    }

    @Transactional
    public void deleteClass(Long id) {
        log.debug("Service: 删除班级, id={}", id);
        if (!gradeClassDao.existsById(id)) {
            log.warn("Service: 班级不存在, id={}", id);
            throw new IllegalArgumentException("班级不存在");
        }
        gradeClassDao.deleteById(id);
    }
}
