package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SemesterRepository extends JpaRepository<Semester, Integer> {
}
