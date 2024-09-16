package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherResponse {
    private int id;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String dob;
    private String gender;
    private String roleName;
    private String status;
    private String image;
    private LocalDateTime createdAt;
}
