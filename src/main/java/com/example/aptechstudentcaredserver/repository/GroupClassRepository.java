package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.entity.GroupClass;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.enums.ClassMemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupClassRepository extends JpaRepository<GroupClass, Integer> {
    Optional<GroupClass> findByUserId(int userId);
    List<GroupClass> findAllByUser(User user);
    List<GroupClass> findByClassesId(int classId);

    List<GroupClass> findByStatus(ClassMemberStatus status);
    @Query("SELECT gc.classes FROM GroupClass gc WHERE gc.user = :user")
    List<Class> findClassesByUser(User user);
}
