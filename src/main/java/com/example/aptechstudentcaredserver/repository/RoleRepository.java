package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    /**
     * find a Role with given name
     *
     * @param name
     * @return Role
     */
    Role findByRoleName(String roleName);

}
