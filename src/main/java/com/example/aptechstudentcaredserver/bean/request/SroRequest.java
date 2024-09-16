package com.example.aptechstudentcaredserver.bean.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SroRequest {
    private String image;
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private String dob;
    private String gender;
    private String address;
    private String status;
}
