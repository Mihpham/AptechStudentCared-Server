package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.response.UserResponse;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.entity.UserDetail;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.UserRepository;
import com.example.aptechstudentcaredserver.service.UserService;
import com.example.aptechstudentcaredserver.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
                .map(this::convertUserToUserResponse)
                .collect(Collectors.toList());
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

    /**
     * Convert User entity to UserResponse.
     *
     * @param user The User entity to convert
     * @return The UserResponse object
     */
    private UserResponse convertUserToUserResponse(User user) {
        // Validate and fix UserDetail if necessary
        UserDetail userDetail = Optional.ofNullable(user.getUserDetail()).orElse(new UserDetail());

        // Build UserResponse
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(Optional.ofNullable(userDetail.getFullName()).orElse("N/A"))
                .phone(Optional.ofNullable(userDetail.getPhone()).orElse("N/A"))
                .address(Optional.ofNullable(userDetail.getAddress()).orElse("N/A"))
                .roleName(Optional.ofNullable(user.getRole()).map(role -> role.getRoleName()).orElse("N/A"))
                .classes(Optional.ofNullable(user.getGroupClasses()).orElse(Collections.emptyList())
                        .stream()
                        .map(groupClass -> Optional.ofNullable(groupClass.getClasses())
                                .map(classes -> classes.getClassName())
                                .orElse("N/A"))
                        .collect(Collectors.toList()))
                .status(String.valueOf(user.getStatus()))
                .roleNumber(Optional.ofNullable(userDetail.getRollNumber()).orElse("N/A"))
                .image(Optional.ofNullable(userDetail.getImage()).orElse("https://static.vecteezy.com/system/resources/previews/043/900/708/non_2x/user-profile-icon-illustration-vector.jpg"))
                .build();
    }
}
