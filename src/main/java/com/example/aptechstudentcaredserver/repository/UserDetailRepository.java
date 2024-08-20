package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailRepository extends JpaRepository<UserDetail, Integer> {
}
