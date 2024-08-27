package com.example.aptechstudentcaredserver.bean.response;

import lombok.Data;

import java.util.List;

@Data
public class StudentResponse {
    private int userId;
    private String fullName;
    private String email;
    private String className;
    private String rollNumber;
    private String phone;
    private String status;
    private List<String> courses;
}
