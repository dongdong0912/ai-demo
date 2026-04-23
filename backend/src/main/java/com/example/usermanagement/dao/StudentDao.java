package com.example.usermanagement.dao;

import com.example.usermanagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentDao extends JpaRepository<Student, Long> {
    
    List<Student> findByClassId(Long classId);
    
    List<Student> findByClassIdAndStatus(Long classId, Integer status);
    
    long countByClassId(Long classId);
    
    long countByClassIdAndStatus(Long classId, Integer status);
    
    long countByStatus(Integer status);
}
