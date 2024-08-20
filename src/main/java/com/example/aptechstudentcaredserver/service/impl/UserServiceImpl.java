package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.response.UserResponse;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.UserRepository;
import com.example.aptechstudentcaredserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private  final UserRepository userRepository;
    @Override
    public UserResponse findUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) {
            User u = user.get();
            UserResponse userResponse = new UserResponse(
                    u.getId(),
                    u.getEmail(),
                    u.getUserDetail().getFullName(),
                    u.getUserDetail().getPhone(),
                    u.getUserDetail().getAddress(),
                    u.getRole().getRoleName()
            );
            return userResponse;
        }
        throw new NotFoundException("User not found id: "+ id);
    }
}
