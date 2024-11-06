package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.Role;
import com.example.aptechstudentcaredserver.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    //    User findByUsername(String username);
    Page<User> findByRoleRoleName(String roleName, Pageable pageable);

    List<User> findByRole(Role role);

    User findByUserDetailFullName(String fullName);

    User findByEmail(String email);
    List<User> findByGroupClassesClassesId(int classId);
    long countByRoleRoleName(String roleName);
}
