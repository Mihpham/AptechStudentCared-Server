package com.example.aptechstudentcaredserver.service;


import com.example.aptechstudentcaredserver.bean.request.StudentRequest;
import com.example.aptechstudentcaredserver.bean.response.StudentResponse;
import com.example.aptechstudentcaredserver.enums.ClassMemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface StudentService {

    public Page<StudentResponse> findAllStudent(Pageable pageable) ;
    public void createStudent(StudentRequest studentRq);
    public List<StudentResponse> findStudentsByStatus(ClassMemberStatus status);
    public StudentResponse findStudentById(int studentId);
    public StudentResponse updateStudent(int studentId, StudentRequest studentRq);
    public void deleteStudent(int studentId);
    public boolean checkRollNumberExists(String rollNumber);
}

