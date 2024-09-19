package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.entity.Subject;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.entity.UserSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubjectRepository extends JpaRepository<UserSubject, Integer> {
    List<UserSubject> findBySubjectIn(List<Subject> subjects);

    List<UserSubject> findByClassroom(Class classroom);


    Optional<UserSubject> findBySubjectAndUser(Subject subject, User user);

    Optional<UserSubject> findBySubject(Subject subject);


}
