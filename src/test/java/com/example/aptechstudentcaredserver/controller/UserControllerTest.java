package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.response.UserResponse;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

/**
 * Các bài kiểm tra cho UserController.
 * <p>
 * Các bài kiểm tra này đảm bảo rằng các điểm cuối của UserController hoạt động chính xác với các phản hồi mong đợi.
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserResponse userResponse;
    private List<UserResponse> userResponses;

    /**
     * Thiết lập dữ liệu mẫu trước khi mỗi bài kiểm tra chạy.
     * <p>
     * Tạo các đối tượng UserResponse mẫu và danh sách các đối tượng này để sử dụng trong các bài kiểm tra.
     * </p>
     */
    @BeforeEach
    void setUp() {
        UserResponse user1 = new UserResponse();
        user1.setId(1);
        user1.setEmail("user1@example.com");
        user1.setFullName("User One");
        user1.setPhone("123456789");
        user1.setAddress("123 Main St");
        user1.setRoleName("USER");
        user1.setStatus("Active");
        user1.setImage("user1.jpg");

        UserResponse user2 = new UserResponse();
        user2.setId(2);
        user2.setEmail("user2@example.com");
        user2.setFullName("User Two");
        user2.setPhone("987654321");
        user2.setAddress("456 Elm St");
        user2.setRoleName("ADMIN");
        user2.setStatus("Inactive");
        user2.setImage("user2.jpg");

        userResponse = user1;
        userResponses = Arrays.asList(user1, user2);
    }

    /**
     * Kiểm tra phương thức GET /api/users với người dùng có vai trò ADMIN.
     * <p>
     * Giả lập hành vi của userService.findAllUser() để trả về danh sách người dùng mẫu và kiểm tra phản hồi HTTP.
     * </p>
     *
     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu HTTP.
     */
    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void getAllUsers_success() throws Exception {
        Mockito.when(userService.findAllUser()).thenReturn(userResponses);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(userResponses.size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("user1@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fullName").value("User One"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].phone").value("123456789"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].address").value("123 Main St"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].roleName").value("USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("Active"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].image").value("user1.jpg"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].email").value("user2@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].fullName").value("User Two"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].phone").value("987654321"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].address").value("456 Elm St"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].roleName").value("ADMIN"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].status").value("Inactive"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].image").value("user2.jpg"));
    }

    /**
     * Kiểm tra phương thức GET /api/users/{id} với ID người dùng hợp lệ.
     * <p>
     * Giả lập hành vi của userService.findUserById() để trả về người dùng mẫu và kiểm tra phản hồi HTTP.
     * </p>
     *
     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu HTTP.
     */
    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void getUserById_existingUser_success() throws Exception {
        Mockito.when(userService.findUserById(1)).thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("user1@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value("User One"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("123456789"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("123 Main St"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roleName").value("USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Active"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.image").value("user1.jpg"));
    }

    /**
     * Kiểm tra phương thức GET /api/users/{id} với ID người dùng không tồn tại.
     * <p>
     * Giả lập hành vi của userService.findUserById() để ném ra ngoại lệ khi người dùng không tồn tại và kiểm tra phản hồi HTTP.
     * </p>
     *
     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu HTTP.
     */
    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void getUserById_nonExistingUser_notFound() throws Exception {
        Mockito.when(userService.findUserById(1)).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    /**
     * Kiểm tra phương thức GET /api/users/profile với token hợp lệ.
     * <p>
     * Giả lập hành vi của userService.findUserResponseFromToken() để trả về người dùng mẫu và kiểm tra phản hồi HTTP.
     * </p>
     *
     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu HTTP.
     */
    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void getUserFromToken_validToken_success() throws Exception {
        String token = "validToken";
        Mockito.when(userService.findUserResponseFromToken(token)).thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/profile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("user1@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value("User One"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("123456789"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("123 Main St"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roleName").value("USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Active"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.image").value("user1.jpg"));
    }

    /**
     * Kiểm tra phương thức GET /api/users/profile với token không hợp lệ.
     * <p>
     * Giả lập hành vi của userService.findUserResponseFromToken() để ném ra ngoại lệ khi token không hợp lệ và kiểm tra phản hồi HTTP.
     * </p>
     *
     * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu HTTP.
     */
    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void getUserFromToken_invalidToken_error() throws Exception {
        String token = "invalidToken";
        Mockito.when(userService.findUserResponseFromToken(token)).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/profile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}
