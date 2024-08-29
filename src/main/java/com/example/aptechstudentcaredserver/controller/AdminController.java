package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.ChangePasswordRequest;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "Welcome to Admin Dashboard!";
    }

    @PatchMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            // Extract the JWT token from the Authorization header
            String jwt = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;

            // Find the user based on the token
            User user = userService.findUserFromToken(jwt);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Change the password
            userService.changePassword(user.getId(), changePasswordRequest);
            return ResponseEntity.ok("Password changed successfully");

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }


}

