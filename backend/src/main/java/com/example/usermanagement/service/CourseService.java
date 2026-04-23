package com.example.usermanagement.service;

import com.example.usermanagement.entity.Course;
import com.example.usermanagement.dao.CourseDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CourseService {

    private final CourseDao courseDao;

    public CourseService(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    public List<Course> getAllCourses() {
        log.debug("Service: 查询所有课程");
        return courseDao.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        log.debug("Service: 根据ID查询课程, id={}", id);
        return courseDao.findById(id);
    }

    public List<Course> searchCourses(String keyword) {
        log.debug("Service: 搜索课程, keyword={}", keyword);
        List<Course> allCourses = courseDao.findAll();
        if (keyword == null || keyword.trim().isEmpty()) {
            return allCourses;
        }
        String lowerKeyword = keyword.toLowerCase().trim();
        return allCourses.stream()
                .filter(c -> {
                    boolean matchName = c.getCourseName() != null && c.getCourseName().toLowerCase().contains(lowerKeyword);
                    boolean matchCode = c.getCourseCode() != null && c.getCourseCode().toLowerCase().contains(lowerKeyword);
                    boolean matchCategory = c.getCategory() != null && c.getCategory().toLowerCase().contains(lowerKeyword);
                    return matchName || matchCode || matchCategory;
                })
                .collect(Collectors.toList());
    }

    public Course createCourse(Course course) {
        log.debug("Service: 创建课程, name={}", course.getCourseName());
        if (course.getCourseCode() != null && courseDao.existsByCourseCode(course.getCourseCode())) {
            throw new IllegalArgumentException("课程编码已存在");
        }
        return courseDao.save(course);
    }

    public Course updateCourse(Long id, Course courseDetails) {
        log.debug("Service: 更新课程, id={}", id);
        Course course = courseDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("课程不存在"));

        if (courseDetails.getCourseCode() != null && !courseDetails.getCourseCode().equals(course.getCourseCode())) {
            if (courseDao.existsByCourseCode(courseDetails.getCourseCode())) {
                throw new IllegalArgumentException("课程编码已存在");
            }
        }

        course.setCourseName(courseDetails.getCourseName());
        course.setCourseCode(courseDetails.getCourseCode());
        course.setType(courseDetails.getType());
        course.setCredit(courseDetails.getCredit());
        course.setTotalHours(courseDetails.getTotalHours());
        course.setCategory(courseDetails.getCategory());
        course.setTeacherId(courseDetails.getTeacherId());
        course.setDescription(courseDetails.getDescription());
        course.setStatus(courseDetails.getStatus());

        return courseDao.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        log.debug("Service: 删除课程, id={}", id);
        if (!courseDao.existsById(id)) {
            log.warn("Service: 课程不存在, id={}", id);
            throw new IllegalArgumentException("课程不存在");
        }
        courseDao.deleteById(id);
    }
}
