package com.example.aptechstudentcaredserver.bean.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserRequest {
    // user
    private String email;
    private String password;

    // user detail
    private String fullName;
    private String phone;
    private String address;
    private String roleName;

}
