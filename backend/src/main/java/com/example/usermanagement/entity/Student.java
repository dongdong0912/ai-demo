package com.example.usermanagement.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_no", nullable = false, unique = true, length = 20)
    private String studentNo;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 10)
    private String gender;

    @Column(name = "id_card", length = 18)
    private String idCard;

    @Column(name = "class_id")
    private Long classId;

    @Column(length = 20)
    private String phone;

    @Column(name = "parent_phone", length = 20)
    private String parentPhone;

    @Column(name = "parent_name", length = 50)
    private String parentName;

    @Column(length = 200)
    private String address;

    private LocalDate birthDate;

    @Column(length = 20)
    private String nation;

    @Column(name = "native_place", length = 50)
    private String nativePlace;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(length = 200)
    private String remark;

    @Column(length = 500)
    private String photo;

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
