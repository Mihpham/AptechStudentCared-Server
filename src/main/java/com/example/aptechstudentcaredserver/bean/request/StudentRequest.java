package com.example.aptechstudentcaredserver.bean.request;

import lombok.Data;

@Data
public class StudentRequest {

    private String rollNumber;
    private String fullName;
    private String password;
    private String gender;
    private String className;
    private String dob;
    private String phoneNumber;
    private String email;
    private String address;
    private String course;
    private String parentFullName;
    private String studentRelation;
    private String parentPhone;
    private String parentGender;
    private String parentJob;

}
