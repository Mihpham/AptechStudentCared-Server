package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    Optional<Subject> findBySubjectName(String subjectName);

    Optional<Subject> findBySubjectCode(String subjectCode);

}
