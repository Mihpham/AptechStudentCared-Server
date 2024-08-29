package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.RegisterUserRequest;
import com.example.aptechstudentcaredserver.bean.response.AuthResponse;
import com.example.aptechstudentcaredserver.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Các bài kiểm tra cho lớp AuthController.
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;  // Inject MockMvc để thực hiện các yêu cầu HTTP mô phỏng

    @MockBean
    private AuthService authService;  // Mock AuthService để kiểm soát hành vi của các phương thức trong lớp này

    private RegisterUserRequest requestAdmin;
    private RegisterUserRequest requestUser;
    private AuthResponse authAdminResponse;
    private AuthResponse authUserResponse;

    /**
     * Khởi tạo dữ liệu mẫu trước mỗi bài kiểm tra.
     */
    @BeforeEach
    void initData() {
        // Dữ liệu cho người dùng với role "user"
        requestUser = RegisterUserRequest.builder()
                .email("hoi.bt.2156@gmail.com")
                .password("123456789")
                .fullName("bui thi hoi")
                .phone("0838658924")
                .address("Ha Noi")
                .roleName("user")
                .build();

        // Phản hồi mong đợi sau khi đăng ký thành công với role "user"
        authUserResponse = AuthResponse.builder()
                .message("Registration successful!")
                .role("user")
                .build();

        // Dữ liệu cho người dùng với role "admin"
        requestAdmin = RegisterUserRequest.builder()
                .email("hoi.bt.2156@gmail.com")
                .password("123456789")
                .fullName("bui thi hoi")
                .phone("0838658924")
                .address("Ha Noi")
                .roleName("admin")
                .build();

        // Phản hồi mong đợi sau khi đăng ký thành công với role "admin"
        authAdminResponse = AuthResponse.builder()
                .message("Registration successful!")
                .role("admin")
                .build();
    }

    /**
     * Kiểm tra kịch bản đăng ký thành công cho người dùng với role "admin".
     * <p>
     * Phương thức này giả lập việc đăng ký thành công và kiểm tra phản hồi từ server.
     * </p>
     */
    @Test
    void registerAdmin_validRequest_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(requestAdmin);
        Mockito.when(authService.registerUser(ArgumentMatchers.any()))
                .thenReturn(authAdminResponse);  // Giả lập phản hồi từ AuthService

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isCreated())  // Kiểm tra mã trạng thái HTTP 201
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Registration successful!"))  // Kiểm tra thông báo phản hồi
                .andExpect(MockMvcResultMatchers.jsonPath("$.role")
                        .value("admin"));  // Kiểm tra vai trò của người dùng
    }

    /**
     * Kiểm tra kịch bản đăng ký thất bại khi email không hợp lệ.
     * <p>
     * Phương thức này thiết lập một email không hợp lệ và kiểm tra phản hồi lỗi từ server.
     * </p>
     */
    @Test
    void registerAdmin_invalidEmailRequest_Fail() throws Exception {
        requestAdmin.setEmail(""); // Cài đặt email không hợp lệ

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(requestAdmin);

        // Thực hiện yêu cầu POST và kiểm tra phản hồi lỗi
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())  // Kiểm tra mã trạng thái HTTP 400
                .andReturn();

        // In ra nội dung phản hồi để debug
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Response content: " + responseContent);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())  // Kiểm tra mã trạng thái HTTP 400
                .andExpect(MockMvcResultMatchers.jsonPath("$.type")
                        .value("about:blank"))  // Kiểm tra loại lỗi
                .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                        .value("Bad Request"))  // Kiểm tra tiêu đề lỗi
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))  // Kiểm tra mã trạng thái lỗi
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail")
                        .value("Invalid request content."));  // Kiểm tra chi tiết lỗi
    }

    /**
     * Kiểm tra kịch bản đăng ký thành công cho người dùng với role "user".
     * <p>
     * Phương thức này giả lập việc đăng ký thành công và kiểm tra phản hồi từ server.
     * </p>
     */
    @Test
    void registerUser_validRequest_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(requestUser);
        Mockito.when(authService.registerUser(ArgumentMatchers.any()))
                .thenReturn(authUserResponse);  // Giả lập phản hồi từ AuthService

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isCreated())  // Kiểm tra mã trạng thái HTTP 201
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Registration successful!"))  // Kiểm tra thông báo phản hồi
                .andExpect(MockMvcResultMatchers.jsonPath("$.role")
                        .value("user"));  // Kiểm tra vai trò của người dùng
    }
}
