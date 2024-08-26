package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.UserCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCourseRepository extends JpaRepository<UserCourse, Integer> {
    List<UserCourse> findByUserId(int userId);

}
