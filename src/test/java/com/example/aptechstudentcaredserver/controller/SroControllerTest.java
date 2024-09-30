package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.SroRequest;
import com.example.aptechstudentcaredserver.bean.response.SroResponse;
import com.example.aptechstudentcaredserver.service.SroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class SroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SroService sroService;

    private ObjectMapper objectMapper = new ObjectMapper();

    // 1. Test registering SRO successfully
     @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testRegisterSroSuccess() throws Exception {
         SroRequest sroRequest = new SroRequest();
         sroRequest.setFullName("John Doe");
         sroRequest.setEmail("johndoe@example.com");
         sroRequest.setPassword("securePassword123");
         sroRequest.setPhoneNumber("0838658924");
         sroRequest.setDob("1990-01-01");
         sroRequest.setGender("Male");
         sroRequest.setAddress("123 Main St");
         // Create a valid SRO request

        mockMvc.perform(post("/api/sros/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sroRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("SRO added successfully"));
    }

    // 2. Test registering SRO with bad data
     @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
     public void testRegisterSroBadRequest() throws Exception {
         SroRequest sroRequest = new SroRequest(); // Invalid SRO request with missing fields

         mockMvc.perform(post("/api/sros/add")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(sroRequest)))
                 .andExpect(status().isBadRequest());
     }


    // 3. Test getting all SROs successfully
     @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testGetAllSrosSuccess() throws Exception {
        List<SroResponse> sroList = List.of(new SroResponse(), new SroResponse());

        when(sroService.findAllSro()).thenReturn(sroList);

        mockMvc.perform(get("/api/sros"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    // 4. Test getting all SROs not found
     @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testGetAllSrosNotFound() throws Exception {
        when(sroService.findAllSro()).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/sros"))
                .andExpect(status().isNotFound());
    }

    // 5. Test getting SRO by ID successfully
     @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testGetSroByIdSuccess() throws Exception {
        SroResponse sroResponse = new SroResponse();

        when(sroService.findSroById(1)).thenReturn(sroResponse);

        mockMvc.perform(get("/api/sros/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // 6. Test getting SRO by ID not found
     @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testGetSroByIdNotFound() throws Exception {
        when(sroService.findSroById(1)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/sros/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("SRO not found with id 1"));
    }

    // 7. Test updating SRO successfully
     @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testUpdateSroSuccess() throws Exception {
         SroRequest sroRequest = new SroRequest();
         sroRequest.setImage("https://example.com/image.jpg");
         sroRequest.setFullName("John Doe");
         sroRequest.setEmail("johndoe@example.com");
         sroRequest.setPassword("securePassword123");
         sroRequest.setPhoneNumber("0838658924");
         sroRequest.setDob("1990-01-01");
         sroRequest.setGender("Male");
         sroRequest.setAddress("123 Main St");
         sroRequest.setStatus("Active");

         SroResponse sroResponse = new SroResponse();

        when(sroService.updateSro(anyInt(), any(SroRequest.class))).thenReturn(sroResponse);

        mockMvc.perform(put("/api/sros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sroRequest)))
                .andExpect(status().isOk());
    }

    // 8. Test updating SRO with invalid data
     @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testUpdateSroBadRequest() throws Exception {
        SroRequest invalidSroRequest = new SroRequest(); // Invalid request

        doThrow(new RuntimeException("Invalid data")).when(sroService).updateSro(anyInt(), any(SroRequest.class));

        mockMvc.perform(put("/api/sros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSroRequest)))
                .andExpect(status().isBadRequest())
   ; }

    // 9. Test updating non-existing SRO
     @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testUpdateSroNotFound() throws Exception {
        SroRequest sroRequest = new SroRequest();

        doThrow(new RuntimeException("SRO not found")).when(sroService).updateSro(anyInt(), any(SroRequest.class));

        mockMvc.perform(put("/api/sros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sroRequest)))
                .andExpect(status().isBadRequest());
   }

    // 10. Test deleting SRO successfully
     @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testDeleteSroSuccess() throws Exception {
        mockMvc.perform(delete("/api/sros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SRO deleted successfully"));
    }

    // 11. Test deleting non-existing SRO
     @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testDeleteSroNotFound() throws Exception {
        doThrow(new RuntimeException("SRO not found")).when(sroService).deleteSro(1);

        mockMvc.perform(delete("/api/sros/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("SRO not found with id 1"));
    }

    // 12. Test getting SRO by invalid ID (non-integer input)
     @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testGetSroInvalidId() throws Exception {
        mockMvc.perform(get("/api/sros/abc"))
                .andExpect(status().isBadRequest());
    }

    // 13. Test updating SRO with null request body
     @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testUpdateSroNullRequest() throws Exception {
        mockMvc.perform(put("/api/sros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // Simulate empty body
                .andExpect(status().isBadRequest());
    }

    // 14. Test registering SRO with null request body
     @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testRegisterSroNullRequest() throws Exception {
        mockMvc.perform(post("/api/sros/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // Simulate empty request body
                .andExpect(status().isBadRequest());
    }

    // 15. Test deleting SRO with invalid ID (non-integer input)
     @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testDeleteSroInvalidId() throws Exception {
        mockMvc.perform(delete("/api/sros/abc"))
                .andExpect(status().isBadRequest());
    }
}

