package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.response.UserResponse;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.mapper.UserMapper;
import com.example.aptechstudentcaredserver.repository.UserRepository;
import com.example.aptechstudentcaredserver.service.UserService;
import com.example.aptechstudentcaredserver.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public List<UserResponse> findAllUser() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            // You can choose to return an empty list with a 200 status
            return Collections.emptyList();
        }
        return users.stream()
                .map(UserMapper::convertToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse findUserById(int id) {
        return userRepository.findById(id)
                .map(UserMapper::convertToUserResponse)
                .orElseThrow(() -> new NotFoundException("User not found id: " + id));
    }

    @Override
    public UserResponse findUserResponseFromToken(String token) {
        User user = findUserFromToken(token);
        if (user != null) {
            return UserMapper.convertToUserResponse(user);
        }
        throw new NotFoundException("User not found for token");
    }

    @Override
    public User findUserFromToken(String token) {
        final String email = jwtUtil.extractUsername(token);
        if (email == null || email.isEmpty()) {
            throw new NotFoundException("Invalid token");
        }
        return userRepository.findByEmail(email);
    }
}

