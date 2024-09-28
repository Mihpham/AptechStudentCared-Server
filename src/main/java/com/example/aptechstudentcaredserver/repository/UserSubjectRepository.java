package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.entity.UserSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubjectRepository extends JpaRepository<UserSubject, Integer> {

    List<UserSubject> findByClassroom(Class classroom);
    Optional<UserSubject> findByClassroomIdAndSubjectId(int classId, int subjectId);

}
