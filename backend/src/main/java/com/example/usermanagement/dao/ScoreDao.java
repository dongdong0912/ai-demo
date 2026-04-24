package com.example.usermanagement.dao;

import com.example.usermanagement.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreDao extends JpaRepository<Score, Long> {
    
    List<Score> findByStudentId(Long studentId);
    
    List<Score> findByCourseId(Long courseId);
    
    List<Score> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    List<Score> findByExamType(String examType);
    
    void deleteByStudentId(Long studentId);
    
    void deleteByCourseId(Long courseId);
}
