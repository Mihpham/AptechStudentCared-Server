package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    Course findByCourseName(String courseName);

    Course findByCourseCode(String courseCode);
}
