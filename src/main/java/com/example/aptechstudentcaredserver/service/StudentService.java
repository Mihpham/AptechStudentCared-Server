package com.example.aptechstudentcaredserver.service;


import com.example.aptechstudentcaredserver.bean.request.RegisterUserRequest;
import com.example.aptechstudentcaredserver.bean.request.StudentRequest;
import com.example.aptechstudentcaredserver.bean.response.AuthResponse;

public interface StudentService {
    AuthResponse createStudent(StudentRequest studentRq);

}
