package com.example.aptechstudentcaredserver.service;


import com.example.aptechstudentcaredserver.bean.request.StudentRequest;
import com.example.aptechstudentcaredserver.bean.response.StudentResponse;

import java.util.List;

public interface StudentService {

    public List<StudentResponse> findAllStudent();
    public void createStudent(StudentRequest studentRq);
    public StudentResponse findStudentById(int studentId);
    public StudentResponse updateStudent(int studentId, StudentRequest studentRq);
    public void deleteStudent(int studentId);
}

