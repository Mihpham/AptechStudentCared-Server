package com.example.aptechstudentcaredserver.bean.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
public class StudentRequest {

    private MultipartFile image;
    private String rollNumber;
    private String fullName;
    private String password;
    private String gender;
    private String className;
    private String dob;
    private String phoneNumber;
    private String email;
    private String address;
    private Set<String> courses;
    private String status;
    private String parentFullName;
    private String studentRelation;
    private String parentPhone;
    private String parentGender;

}
