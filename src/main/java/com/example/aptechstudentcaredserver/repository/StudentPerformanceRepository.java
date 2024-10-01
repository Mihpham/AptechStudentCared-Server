package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.StudentPerformance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentPerformanceRepository extends JpaRepository<StudentPerformance,Integer> {
    Optional<StudentPerformance> findByUserIdAndSubjectId(int userId, int subjectId);

}
