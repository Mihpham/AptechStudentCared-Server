package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Class, Integer>, JpaSpecificationExecutor<Class> {
    Class findByClassName(String className);
    Page<Class> findByStatus(Status status, Pageable pageable);
    public Page<Class> findBySemester(String sem, Pageable pageable);
    List<Class> findByCourseId(int courseId);
}
