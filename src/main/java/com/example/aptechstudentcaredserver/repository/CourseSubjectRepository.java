package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.CourseSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseSubjectRepository extends JpaRepository<CourseSubject, Integer> {
    List<CourseSubject> findByCourseId(int courseId);

    List<CourseSubject>findBySubjectId(int subjectId);
}
