package com.example.aptechstudentcaredserver.bean.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequest {
    // user
    private String email;
    private String password;

    // user detail
    private String fullName;
    private String phone;
    private String address;
    private String roleName;
    private String image;
    

}
