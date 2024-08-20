package com.example.aptechstudentcaredserver.bean.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
