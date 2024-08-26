package com.example.aptechstudentcaredserver.service;


import com.example.aptechstudentcaredserver.bean.request.StudentRequest;
import com.example.aptechstudentcaredserver.bean.response.StudentResponse;

public interface StudentService {
    public void createStudent(StudentRequest studentRq);
    public StudentResponse findStudentById(int studentId);
}
