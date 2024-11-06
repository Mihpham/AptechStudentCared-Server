package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.ChangePasswordRequest;
import com.example.aptechstudentcaredserver.bean.request.TeacherRequest;
import com.example.aptechstudentcaredserver.bean.response.UpdateUserStatusResponse;
import com.example.aptechstudentcaredserver.bean.response.UserResponse;
import com.example.aptechstudentcaredserver.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    List<UserResponse> findAllUser();

    UserResponse findUserById(int id);

    User findUserFromToken(String token);

    UserResponse findUserResponseFromToken(String token);

    void changePassword(int userId, ChangePasswordRequest changePasswordRequest);

    public Page<UserResponse> findUsersByRoleName(String roleName, Pageable pageable) ;

    public UpdateUserStatusResponse updateUserStatus(int userId);

    long countUsersByRoleName(String roleName);
}
