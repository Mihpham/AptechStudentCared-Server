package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.response.UserResponse;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.UserRepository;
import com.example.aptechstudentcaredserver.service.UserService;
import com.example.aptechstudentcaredserver.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            throw new NotFoundException("No users found. Please add users to the system and try again.");
        }
        return users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }


    @Override
    public UserResponse findUserById(int id) {
        return userRepository.findById(id)
                .map(this::convertToUserResponse)
                .orElseThrow(() -> new NotFoundException("User not found id: " + id));
    }

    @Override
    public UserResponse findUserResponseFromToken(String token) {
        User user = findUserFromToken(token);
        if (user != null) {
            return convertToUserResponse(user);
        }
        throw new NotFoundException("User not found for token");
    }

    @Override
    public User findUserFromToken(String token) {
        final String email = jwtUtil.extractUsername(token);
        return userRepository.findByEmail(email);
    }

    private UserResponse convertToUserResponse(User user) {
        List<String> classNames = user.getGroupClasses().stream()
                .map(groupClass -> groupClass.getClasses().getClassName())
                .collect(Collectors.toList());
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUserDetail().getFullName(),
                user.getUserDetail().getPhone(),
                user.getUserDetail().getAddress(),
                user.getRole().getRoleName(),
                classNames,
                String.valueOf(user.getStatus()),
                user.getUserDetail().getRoleNumber(),
                user.getUserDetail().getImage()
        );
    }

}
