package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.response.UserResponse;
import com.example.aptechstudentcaredserver.entity.User;

import java.util.List;

public interface UserService {
    List<UserResponse> findAllUser();
    UserResponse findUserById(int id);
    User findUserFromToken(String token);
    UserResponse findUserResponseFromToken(String token);

}
