package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
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
    private String phoneNumber;
    private List<String> courses;
    private String status;
    private String parentFullName;
    private String studentRelation;
    private String parentPhone;
    private String parentGender;

}
