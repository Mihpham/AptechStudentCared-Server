package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.ChangePasswordRequest;
import com.example.aptechstudentcaredserver.bean.response.UserResponse;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.UserRepository;
import com.example.aptechstudentcaredserver.service.CloudinaryService;
import com.example.aptechstudentcaredserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
