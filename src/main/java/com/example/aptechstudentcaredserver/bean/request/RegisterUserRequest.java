package com.example.aptechstudentcaredserver.bean.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String address;
    private String roleName;
}
