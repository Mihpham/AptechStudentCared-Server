package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.AuthRequest;
import com.example.aptechstudentcaredserver.bean.request.RegisterUserRequest;
import com.example.aptechstudentcaredserver.bean.response.AuthResponse;

public interface AuthService {

    AuthResponse registerUser(RegisterUserRequest registerUserRequest)  ;
    AuthResponse loginUser(AuthRequest authRequest) ;


}
