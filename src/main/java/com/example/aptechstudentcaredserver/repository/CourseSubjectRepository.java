package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.CourseSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseSubjectRepository extends JpaRepository<CourseSubject, Integer> {
}
