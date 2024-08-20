package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.AuthRequest;
import com.example.aptechstudentcaredserver.bean.request.RegisterUserRequest;
import com.example.aptechstudentcaredserver.bean.response.AuthResponse;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.exception.EmailFormatException;
import com.example.aptechstudentcaredserver.exception.InvalidCredentialsException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
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
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest authRequest) {
        try {
            AuthResponse authResponse = authService.loginUser(authRequest);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (EmailFormatException e) {
            throw e;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }
}




