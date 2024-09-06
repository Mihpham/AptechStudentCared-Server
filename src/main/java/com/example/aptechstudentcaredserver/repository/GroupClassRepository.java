package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.GroupClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupClassRepository extends JpaRepository<GroupClass, Integer> {
    Optional<GroupClass> findByUserId(int userId);

    List<GroupClass> findByClassesId(int classId); // Sửa lại tên phương thức


}
