package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentInClassResponse {
    private int userId;
    private String image;
    private String rollNumber;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String status;
}
