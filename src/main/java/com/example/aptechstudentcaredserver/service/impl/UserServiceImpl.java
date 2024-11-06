package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.ChangePasswordRequest;
import com.example.aptechstudentcaredserver.bean.response.UpdateUserStatusResponse;
import com.example.aptechstudentcaredserver.bean.response.UserResponse;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.entity.UserDetail;
import com.example.aptechstudentcaredserver.enums.Status;
import com.example.aptechstudentcaredserver.exception.EmptyListException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.RoleRepository;
import com.example.aptechstudentcaredserver.repository.UserDetailRepository;
import com.example.aptechstudentcaredserver.repository.UserRepository;
import com.example.aptechstudentcaredserver.service.EmailGeneratorService;
import com.example.aptechstudentcaredserver.service.UserService;
import com.example.aptechstudentcaredserver.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailGeneratorService emailGeneratorService;
    private final RoleRepository roleRepository;


    @Override
    public List<UserResponse> findAllUser() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new EmptyListException("users not found");
        }
        return users.stream()
                .map(this::convertUserToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserResponse> findUsersByRoleName(String roleName, Pageable pageable) {
        // Fetch paginated users by role name
        Page<User> users = userRepository.findByRoleRoleName(roleName, pageable);

        if (!users.hasContent()) {
            throw new EmptyListException("No users found with role: " + roleName);
        }

        // Map users to UserResponse and return as Page
        return users.map(this::convertUserToUserResponse);
    }


    @Override
    public long countUsersByRoleName(String roleName) {
        return userRepository.countByRoleRoleName(roleName);
    }

    @Override
    public UpdateUserStatusResponse updateUserStatus(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        // Toggle the user's status
        if (user.getStatus() == Status.ACTIVE) {
            user.setStatus(Status.INACTIVE);
        } else {
            user.setStatus(Status.ACTIVE);
        }

        userRepository.save(user);

        // Return response indicating success
        return UpdateUserStatusResponse.builder()
                .message("User status updated successfully.")
                .updatedStatus(user.getStatus())
                .build();
    }

    @Override
    public UserResponse findUserById(int id) {
        return userRepository.findById(id)
                .map(this::convertUserToUserResponse)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    @Override
    public UserResponse findUserResponseFromToken(String token) {
        User user = findUserFromToken(token);
        if (user != null) {
            return convertUserToUserResponse(user);
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

    @Override
    public void changePassword(int userId, ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }


    private UserResponse convertUserToUserResponse(User user) {
        String fullName = Optional.ofNullable(user.getUserDetail()).map(d -> d.getFullName()).orElse("N/A");
        String phone = Optional.ofNullable(user.getUserDetail()).map(d -> d.getPhone()).orElse("N/A");
        String address = Optional.ofNullable(user.getUserDetail()).map(d -> d.getAddress()).orElse("N/A");


        return UserResponse.builder()
                .id(user.getId())
                .email(Optional.ofNullable(user.getEmail()).orElse("N/A"))
                .fullName(fullName)
                .phone(phone)
                .address(address)
                .roleName(Optional.ofNullable(user.getRole()).map(r -> r.getRoleName()).orElse("N/A"))
                .status(Optional.ofNullable(user.getStatus()).map(Enum::name).orElse("N/A"))
                .image(user.getUserDetail().getImage())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
