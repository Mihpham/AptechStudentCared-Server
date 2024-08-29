package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse {
    private int userId;
    private String image;
    private String rollNumber;
    private String fullName;
    private String email;
    private String address;
    private String className;
    private String gender;
    private LocalDateTime dob;
    private String phoneNumber;
    private List<String> courses;
    private String status;
    private String parentFullName;
    private String studentRelation;
    private String parentPhone;
    private String parentGender;

}
