package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.response.UserResponse;

public interface UserService {
    UserResponse findUserById (int id);

}
