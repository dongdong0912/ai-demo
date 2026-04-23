package com.example.usermanagement.dao;

import com.example.usermanagement.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseDao extends JpaRepository<Course, Long> {
    
    long countByStatus(Integer status);
    
    boolean existsByCourseCode(String courseCode);
    
    long countByCategory(String category);
}
