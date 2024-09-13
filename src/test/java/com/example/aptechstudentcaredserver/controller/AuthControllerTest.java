package com.example.aptechstudentcaredserver.controller;


import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.aptechstudentcaredserver.bean.request.AuthRequest;
import com.example.aptechstudentcaredserver.bean.request.RegisterUserRequest;
import com.example.aptechstudentcaredserver.bean.response.AuthResponse;
import com.example.aptechstudentcaredserver.exception.EmailFormatException;
import com.example.aptechstudentcaredserver.exception.InvalidCredentialsException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.service.AuthService;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Lớp test cho AuthController.
 * Kiểm tra các chức năng của API: đăng ký và đăng nhập.
 */
/**
 * Lớp test cho AuthController với Spring Boot Test.
 */
//@SpringBootTest // Khởi chạy toàn bộ context của Spring Boot
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc; // Inject MockMvc để giả lập các request HTTP

    @MockBean
    private AuthService authService; // Giả lập AuthService để không gọi đến service thực

    @Autowired
    private ObjectMapper objectMapper; // Sử dụng ObjectMapper để chuyển đổi đối tượng thành JSON

    /**
     * Test đăng ký người dùng thành công.
     * Kết quả mong muốn: Trả về HttpStatus.CREATED và thông tin phản hồi chứa JWT.
     */
    @Test
    void testRegisterUser_ShouldReturnCreated_WhenRegistrationSuccessful() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .email("test@example.com")
                .password("password123")
                .fullName("Nguyễn Văn A")
                .phone("0123456789")
                .address("123 Đường ABC, Hà Nội")
                .roleName("USER")
                .image("profile.jpg")
                .build();

        AuthResponse response = AuthResponse.builder()
                .jwt("jwt-token")
                .message("Registration successful")
                .role("USER")
                .build();

        when(authService.registerUser(any(RegisterUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jwt").value("jwt-token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Registration successful"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("USER"));
    }

    /**
     * Test đăng ký thất bại khi email đã tồn tại.
     * Kết quả mong muốn: Trả về HttpStatus.CONFLICT và thông báo lỗi.
     */
    @Test
    void testRegisterUser_ShouldReturnConflict_WhenDuplicateEmail() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .email("duplicate@example.com")
                .password("password123")
                .fullName("Nguyễn Văn B")
                .phone("0987654321")
                .address("456 Đường XYZ, Đà Nẵng")
                .roleName("USER")
                .image("avatar.jpg")
                .build();

        when(authService.registerUser(any(RegisterUserRequest.class)))
                .thenThrow(new DuplicateException("Email already exists"));

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    /**
     * Test đăng nhập thành công.
     * Kết quả mong muốn: Trả về HttpStatus.OK và thông tin JWT.
     */
    @Test
    void testLoginUser_ShouldReturnOk_WhenLoginSuccessful() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        AuthResponse response = AuthResponse.builder()
                .jwt("jwt-token")
                .message("Login successful")
                .role("USER")
                .build();

        when(authService.loginUser(any(AuthRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("jwt-token"))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    /**
     * Test đăng nhập thất bại khi email không tồn tại.
     * Kết quả mong muốn: Trả về HttpStatus.NOT_FOUND.
     */
    @Test
    void testLoginUser_ShouldReturnNotFound_WhenEmailNotFound() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .email("nonexistent@example.com")
                .password("password123")
                .build();

        when(authService.loginUser(any(AuthRequest.class)))
                .thenThrow(new NotFoundException("Email not found"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Email not found"));
    }

    /**
     * Test đăng nhập thất bại khi thông tin đăng nhập sai.
     * Kết quả mong muốn: Trả về HttpStatus.UNAUTHORIZED.
     */
    @Test
    void testLoginUser_ShouldReturnUnauthorized_WhenInvalidCredentials() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .email("test@example.com")
                .password("wrongpassword")
                .build();

        when(authService.loginUser(any(AuthRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    /**
     * Test đăng ký thất bại khi dữ liệu không hợp lệ.
     * Kết quả mong muốn: Trả về HttpStatus.BAD_REQUEST.
     */
    @Test
    void testRegisterUser_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .email("invalid-email")
                .password("password123")
                .fullName("Nguyễn Văn C")
                .phone("12345") // Số điện thoại không hợp lệ
                .address("456 Đường XYZ, Đà Nẵng")
                .roleName("USER")
                .image("avatar.jpg")
                .build();

        when(authService.registerUser(any(RegisterUserRequest.class)))
                .thenThrow(new EmailFormatException("Invalid email format"));

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User registration failed: Invalid email format"));
    }
}