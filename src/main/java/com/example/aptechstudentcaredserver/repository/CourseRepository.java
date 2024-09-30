package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.*;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    Course findByCourseName(String courseName);
    Course findByCourseCode(String courseCode);
    boolean existsByCourseNameAndIdNot(String courseName, int courseId);
    boolean existsByCourseCodeAndIdNot(String courseCode, int courseId);
}
