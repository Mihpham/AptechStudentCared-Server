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

    @BeforeEach
    void initData() {
        // Khởi tạo dữ liệu mẫu trước mỗi bài kiểm tra

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

    @Test
    void registerAdmin_validRequest_success() throws Exception {
        // Kiểm tra kịch bản đăng ký thành công cho người dùng với role "admin"

        // GIVEN: Cài đặt dữ liệu đầu vào và phản hồi mong đợi
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(requestAdmin);
        Mockito.when(authService.registerUser(ArgumentMatchers.any()))
                .thenReturn(authAdminResponse);  // Giả lập phản hồi từ AuthService

        // WHEN: Thực hiện yêu cầu POST đến endpoint "/auth/signup"
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                // THEN: Kiểm tra phản hồi nhận được
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Registration successful!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role")
                        .value("admin"));
    }

    @Test
    void registerAdmin_invalidEmailRequest_Fail() throws Exception {
        // Kiểm tra kịch bản đăng ký thất bại khi email không hợp lệ

        requestAdmin.setEmail(""); // Cài đặt email không hợp lệ

        // GIVEN: Cài đặt dữ liệu đầu vào
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(requestAdmin);

        // WHEN: Thực hiện yêu cầu POST đến endpoint "/auth/signup"
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        // THEN: In ra nội dung phản hồi để debug
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Response content: " + responseContent);

        // THEN: Kiểm tra phản hồi nhận được
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.type")
                        .value("about:blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail")
                        .value("Invalid request content."));
    }

    @Test
    void registerUser_validRequest_success() throws Exception {
        // Kiểm tra kịch bản đăng ký thành công cho người dùng với role "user"

        // GIVEN: Cài đặt dữ liệu đầu vào và phản hồi mong đợi
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(requestUser);
        Mockito.when(authService.registerUser(ArgumentMatchers.any()))
                .thenReturn(authUserResponse);  // Giả lập phản hồi từ AuthService

        // WHEN: Thực hiện yêu cầu POST đến endpoint "/auth/signup"
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                // THEN: Kiểm tra phản hồi nhận được
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Registration successful!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role")
                        .value("user"));
    }
}
