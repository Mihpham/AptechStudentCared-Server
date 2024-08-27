package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.response.UserResponse;
import com.example.aptechstudentcaredserver.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private List<UserResponse> userResponses;

    @BeforeEach
    void setUp() {
        UserResponse user1 = new UserResponse();
        user1.setId(1);
        user1.setEmail("user1@example.com");
        user1.setFullName("User One");
        user1.setPhone("123456789");
        user1.setAddress("123 Main St");
        user1.setRoleName("USER");
        user1.setClasses(Arrays.asList("Class A", "Class B"));
        user1.setStatus("Active");
        user1.setRoleNumber("001");
        user1.setImage("user1.jpg");

        UserResponse user2 = new UserResponse();
        user2.setId(2);
        user2.setEmail("user2@example.com");
        user2.setFullName("User Two");
        user2.setPhone("987654321");
        user2.setAddress("456 Elm St");
        user2.setRoleName("ADMIN");
        user2.setClasses(Arrays.asList("Class C"));
        user2.setStatus("Inactive");
        user2.setRoleNumber("002");
        user2.setImage("user2.jpg");

        userResponses = Arrays.asList(user1, user2);
    }

    @Test
    @WithMockUser(username = "ggggg@gmail.com", roles = {"ADMIN"},password = "quang123s")
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
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].classes[0]").value("Class A"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].classes[1]").value("Class B"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("Active"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].roleNumber").value("001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].image").value("user1.jpg"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].email").value("user2@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].fullName").value("User Two"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].phone").value("987654321"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].address").value("456 Elm St"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].roleName").value("ADMIN"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].classes[0]").value("Class C"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].status").value("Inactive"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].roleNumber").value("002"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].image").value("user2.jpg"));
    }
}


