package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.ChangePasswordRequest;
import com.example.aptechstudentcaredserver.bean.response.UpdateUserStatusResponse;
import com.example.aptechstudentcaredserver.bean.response.UserResponse;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.exception.EmptyListException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.UserRepository;
import com.example.aptechstudentcaredserver.service.CloudinaryService;
import com.example.aptechstudentcaredserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        try {
            List<UserResponse> users = userService.findAllUser();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/role/{roleName}")
    public ResponseEntity<Page<UserResponse>> getUsersByRoleName(
            @PathVariable String roleName,
            @RequestParam(defaultValue = "0") int page,   // Default to page 0 if not provided
            @RequestParam(defaultValue = "10") int size    // Default to 10 items per page if not provided
    ) {
        Pageable pageable = PageRequest.of(page, size);  // Create Pageable from request parameters
        Page<UserResponse> users = userService.findUsersByRoleName(roleName, pageable);
        return ResponseEntity.ok(users);
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<UpdateUserStatusResponse> updateUserStatus(@PathVariable int id) {
        UpdateUserStatusResponse response = userService.updateUserStatus(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/role/total/{roleName}")
    public ResponseEntity<Map<String, Long>> countUsersByRoleName(@PathVariable String roleName) {
        try {
            long totalAccount = userService.countUsersByRoleName(roleName);

            // Sử dụng HashMap để đảm bảo kiểu chính xác
            Map<String, Long> response = new HashMap<>();
            response.put("totalAccount", totalAccount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Trả về lỗi hệ thống
            Map<String, Long> response = new HashMap<>();
            response.put("totalAccount", 0L);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable int id) {
        UserResponse userResponse = userService.findUserById(id);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserFromToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String jwt = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
            UserResponse userResponse = userService.findUserResponseFromToken(jwt);
            return ResponseEntity.ok(userResponse);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PatchMapping("/{id}/image")
    public ResponseEntity<?> updateImage(@PathVariable Long id, @RequestParam("image") MultipartFile imageFile) {
        Optional<User> optionalUser = userRepository.findById(Math.toIntExact(id));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            try {
                // Tải tệp lên Cloudinary và lấy URL
                String imageUrl = cloudinaryService.uploadToCloudinary(imageFile);

                // Cập nhật URL ảnh vào đối tượng UserDetail
                user.getUserDetail().setImage(imageUrl);

                // Lưu User đã cập nhật vào cơ sở dữ liệu
                userRepository.save(user);

                return ResponseEntity.ok("Image updated successfully");
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @PatchMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            // Extract the JWT token from the Authorization header
            String jwt = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;

            // Find the user based on the token
            User user = userService.findUserFromToken(jwt);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
            }

            // Change the password
            userService.changePassword(user.getId(), changePasswordRequest);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An error occurred"));
        }
    }

}
