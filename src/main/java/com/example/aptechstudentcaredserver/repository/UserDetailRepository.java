package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDetailRepository extends JpaRepository<UserDetail, Integer> {
    Optional<UserDetail> findByFullName(String fullName);
    boolean existsByRollNumber(String rollNumber);
}
