package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SemesterRepository extends JpaRepository<Semester, Integer> {
    Optional<Semester> findByName(String name);

}
