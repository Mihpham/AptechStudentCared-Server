package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Class, Integer> {
    Class findByClassName(String className);
    List<Class> findByCourseId(int courseId);
}
