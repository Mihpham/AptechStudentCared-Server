package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.GroupClass;
import com.example.aptechstudentcaredserver.enums.ClassMemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupClassRepository extends JpaRepository<GroupClass, Integer> {
    Optional<GroupClass> findByUserId(int userId);

    List<GroupClass> findByClassesId(int classId);

    List<GroupClass> findByStatus(ClassMemberStatus status);
}
