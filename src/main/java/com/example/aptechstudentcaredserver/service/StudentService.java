package com.example.aptechstudentcaredserver.service;


import com.example.aptechstudentcaredserver.bean.request.StudentRequest;
import com.example.aptechstudentcaredserver.bean.response.StudentResponse;
import com.example.aptechstudentcaredserver.enums.ClassMemberStatus;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface StudentService {

    public List<StudentResponse> findAllStudent();
    public void createStudent(StudentRequest studentRq);
    public List<StudentResponse> findStudentsByStatus(ClassMemberStatus status);
    public StudentResponse findStudentById(int studentId);
    public StudentResponse updateStudent(int studentId, StudentRequest studentRq);
    public void deleteStudent(int studentId);
}

