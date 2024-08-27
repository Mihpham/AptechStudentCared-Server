package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    //    User findByUsername(String username);
    List<User> findByRoleRoleName(String roleName);

    User findByEmail(String email);

}
