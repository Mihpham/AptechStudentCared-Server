package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private int id;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String roleName;
    private String status;
    private String image;
    private LocalDateTime createdAt;
}
