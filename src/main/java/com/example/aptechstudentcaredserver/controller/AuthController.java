package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.AuthRequest;
import com.example.aptechstudentcaredserver.bean.request.RegisterUserRequest;
import com.example.aptechstudentcaredserver.bean.response.AuthResponse;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterUserRequest registerUserRequest) {
        try {
            AuthResponse authResponse = authService.registerUser(registerUserRequest);
            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
        } catch (DuplicateException e) {
            return new ResponseEntity<>(new AuthResponse(null, e.getMessage(), null), HttpStatus.CONFLICT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new AuthResponse(null, "User registration failed: " + e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody AuthRequest authRequest) {
        try {
            AuthResponse authResponse = authService.loginUser(authRequest);
            if (authResponse.getJwt() == null) {
                HttpStatus status = authResponse.getMessage().contains("Invalid credentials") ? HttpStatus.UNAUTHORIZED : HttpStatus.BAD_REQUEST;
                return new ResponseEntity<>(authResponse, status);
            }
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new AuthResponse(null, "Login failed: " + e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }
}




