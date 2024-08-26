package com.example.aptechstudentcaredserver.bean.response;

import com.example.aptechstudentcaredserver.entity.GroupClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private int id;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String roleName;
    private List<String> classes;
    private String status;
    private String roleNumber;
    private String image;
}
