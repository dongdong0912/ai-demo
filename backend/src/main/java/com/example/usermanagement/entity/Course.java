package com.example.usermanagement.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_name", nullable = false, length = 50)
    private String courseName;

    @Column(name = "course_code", unique = true, length = 20)
    private String courseCode;

    @Column(length = 10)
    private String type;

    private Double credit;

    @Column(name = "total_hours")
    private Integer totalHours;

    @Column(length = 50)
    private String category;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer status = 1;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
