package com.example.aptechstudentcaredserver.bean.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherRequest {
    private String image;
    private String fullName;
    private String password;
    private String phoneNumber;
    private String dob;
    private String address;
    private String status;
}
