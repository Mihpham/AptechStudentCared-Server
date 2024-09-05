package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
}
