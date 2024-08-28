package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.response.UserResponse;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.entity.UserDetail;
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
            return Collections.emptyList(); // Return empty list if no users found
        }
        return users.stream()
                .map(user -> {
                    try {
                        // Validate and fix user data if necessary before mapping
                        User validatedUser = validateUser(user);
                        return UserMapper.convertToUserResponse(validatedUser);
                    } catch (NotFoundException e) {
                        // Log the error and handle it based on your requirements
                        System.err.println("Error mapping user to response: " + e.getMessage());
                        return null; // or handle as needed
                    }
                })
                .filter(userResponse -> userResponse != null) // Remove any null responses
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse findUserById(int id) {
        return userRepository.findById(id)
                .map(user -> {
                    try {
                        // Validate and fix user data if necessary before mapping
                        User validatedUser = validateUser(user);
                        return UserMapper.convertToUserResponse(validatedUser);
                    } catch (NotFoundException e) {
                        // Log the error and handle it based on your requirements
                        System.err.println("Error mapping user to response: " + e.getMessage());
                        throw new NotFoundException("User found but could not be mapped: " + e.getMessage());
                    }
                })
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    @Override
    public UserResponse findUserResponseFromToken(String token) {
        User user = findUserFromToken(token);
        if (user != null) {
            try {
                // Validate and fix user data if necessary before mapping
                User validatedUser = validateUser(user);
                return UserMapper.convertToUserResponse(validatedUser);
            } catch (NotFoundException e) {
                // Log the error and handle it based on your requirements
                System.err.println("Error mapping user to response: " + e.getMessage());
                throw new NotFoundException("User found but could not be mapped: " + e.getMessage());
            }
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

    /**
     * Validate and provide default values for the User entity to prevent mapping issues.
     *
     * @param user The User entity to validate
     * @return The validated User entity
     */
    private User validateUser(User user) {
        if (user.getUserDetail() == null) {
            user.setUserDetail(new UserDetail());
        }
        UserDetail userDetail = user.getUserDetail();
        if (userDetail.getRollNumber() == null) {
            userDetail.setRollNumber("N/A");
        }
        if (userDetail.getImage() == null) {
            userDetail.setImage("avatar.jpg");
        }
        return user;
    }
}
