package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherResponse {
    private String image;
    private String fullName;
    private String password;
    private String phoneNumber;
    private String address;
    private String status;
}
