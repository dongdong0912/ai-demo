package com.example.usermanagement.dao;

import com.example.usermanagement.entity.GradeClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeClassDao extends JpaRepository<GradeClass, Long> {
    
    long countByStatus(Integer status);
}
