package com.example.aptechstudentcaredserver.bean.request;

import com.example.aptechstudentcaredserver.entity.GroupClass;
import com.example.aptechstudentcaredserver.entity.UserCourse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class StudentRequest {

    private String rollNumber;
    private String fullName;
    private String gender;
    private Set<GroupClass> groupClasses;
    private LocalDateTime dob;
    private String phoneNumber;
    private String email;
    private String password;
    private String address;
    private Set<UserCourse> userCourses;
    private String parentFullName;
    private String parentPhone;
    private String parentAddress;
    private String parentGender;
    private String parentJob;

}
