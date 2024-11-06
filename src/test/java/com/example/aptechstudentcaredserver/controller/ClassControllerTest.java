//package com.example.aptechstudentcaredserver.controller;
//
//import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
//import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
//import com.example.aptechstudentcaredserver.bean.response.ResponseMessage;
//import com.example.aptechstudentcaredserver.enums.DayOfWeeks;
//import com.example.aptechstudentcaredserver.exception.NotFoundException;
//import com.example.aptechstudentcaredserver.service.ClassService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//@SpringBootTest
//@AutoConfigureMockMvc
//public class ClassControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private ClassService classService;
//
//    @InjectMocks
//    private ClassController classController;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(classController).build();
//    }
//
//    // 1. GET Requests
//
//    @Test
//    public void testGetAllClasses_ShouldReturnListOfClasses() throws Exception {
//        List<ClassResponse> classResponses = List.of(new ClassResponse(), new ClassResponse());
//        when(classService.findAllClass()).thenReturn(classResponses);
//
//        mockMvc.perform(get("/api/classes"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(2));
//
//        verify(classService, times(1)).findAllClass();
//    }
//
//    @Test
//    public void testGetClassById_ShouldReturnClass() throws Exception {
//        ClassResponse classResponse = new ClassResponse();
//        classResponse.setId(1);
//        classResponse.setClassName("Class A");
//
//        when(classService.findClassById(1)).thenReturn(classResponse);
//
//        mockMvc.perform(get("/api/classes/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.className").value("Class A"));
//
//        verify(classService, times(1)).findClassById(1);
//    }
//
//    @Test
//    public void testGetClassById_ShouldReturnNotFound_WhenResourceDoesNotExist() throws Exception {
//        when(classService.findClassById(1)).thenReturn(null);
//
//        mockMvc.perform(get("/api/classes/1"))
//                .andExpect(status().isNotFound());
//
//        verify(classService, times(1)).findClassById(1);
//    }
//
//    // 2. POST Requests
//
//    @Test
//    public void testCreateClass_ShouldReturnCreatedClass() throws Exception {
//        ClassRequest classRequest = new ClassRequest();
//        classRequest.setClassName("Class A");
//        classRequest.setCenter("Center 1");
//        classRequest.setStartHour(LocalTime.of(8, 30)); ;  // 8:30 AM
//        classRequest.setEndHour(LocalTime.of(10, 0)); ;    // 10:00 AM
//        classRequest.setDays(Arrays.asList(DayOfWeeks.MONDAY, DayOfWeeks.WEDNESDAY));
//        classRequest.setStatus("ACTIVE"); // Set status
//        classRequest.setSem("Fall 2024"); // Set semester
//        classRequest.setCourseCode("CS101"); // Set course code
//
////        when(classService.addClass(any(ClassRequest.class))).thenReturn("Class added successfully");
//
//        mockMvc.perform(post("/api/classes/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(classRequest)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.message").value("Class added successfully"));
//
//        verify(classService, times(1)).addClass(any(ClassRequest.class));
//    }
//
//
//    @Test
//    public void testCreateClass_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
//        ClassRequest classRequest = new ClassRequest(); // Invalid data (empty fields)
//
//        mockMvc.perform(post("/api/classes/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(classRequest)))
//                .andExpect(status().isBadRequest());
//
//        verify(classService, never()).addClass(any(ClassRequest.class));
//    }
//
//    // 3. PUT Requests
//
//    @Test
//    public void testUpdateClass_ShouldReturnUpdatedClass() throws Exception {
//        ClassRequest classRequest = new ClassRequest();
//        classRequest.setClassName("Class B");
//        classRequest.setCenter("Main Center"); // Add all required fields
//        classRequest.setStartHour(LocalTime.of(8, 30)); ;  // 8:30 AM
//        classRequest.setEndHour(LocalTime.of(10, 0)); ;    // 10:00 AM
//        classRequest.setDays(Arrays.asList(DayOfWeeks.MONDAY, DayOfWeeks.WEDNESDAY));
//        classRequest.setStatus("ACTIVE");
//        classRequest.setSem("Fall 2024");
//        classRequest.setCourseCode("CS101");
//
//        ClassResponse classResponse = new ClassResponse();
//        classResponse.setId(1);
//        classResponse.setClassName("Class B");
//
//        when(classService.updateClass(eq(1), any(ClassRequest.class))).thenReturn(classResponse);
//
//        mockMvc.perform(put("/api/classes/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(classRequest)))
//                .andExpect(status().isAccepted())
//                .andExpect(jsonPath("$.className").value("Class B"));
//
//        verify(classService, times(1)).updateClass(eq(1), any(ClassRequest.class));
//    }
//
//
//
//    @Test
//    public void testUpdateClass_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
//        ClassRequest classRequest = new ClassRequest(); // Invalid data
//
//        mockMvc.perform(put("/api/classes/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(classRequest)))
//                .andExpect(status().isBadRequest());
//
//        verify(classService, never()).updateClass(eq(1), any(ClassRequest.class));
//    }
//
//    // 4. DELETE Requests
//
//    @Test
//    public void testDeleteClass_ShouldReturnNoContent() throws Exception {
//        doNothing().when(classService).deleteClass(1);
//
//        mockMvc.perform(delete("/api/classes/1"))
//                .andExpect(status().isAccepted());
//
//        verify(classService, times(1)).deleteClass(1);
//    }
//
//    @Test
//    public void testDeleteClass_ShouldReturnNotFound_WhenResourceDoesNotExist() throws Exception {
//        doThrow(new NotFoundException("Class not found")).when(classService).deleteClass(1); // Throw NotFoundException
//
//        mockMvc.perform(delete("/api/classes/1"))
//                .andExpect(status().isNotFound()); // Expect 404
//
//        verify(classService, times(1)).deleteClass(1);
//    }
//
//
//    // 5. Edge Cases and Error Handling
//
//    @Test
//    public void testGetAllClasses_ShouldReturnEmptyList_WhenNoResources() throws Exception {
//        when(classService.findAllClass()).thenReturn(List.of());
//
//        mockMvc.perform(get("/api/classes"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(0));
//
//        verify(classService, times(1)).findAllClass();
//    }
//
//    @Test
//    public void testCreateClass_ShouldHandleNullRequestBody() throws Exception {
//        mockMvc.perform(post("/api/classes/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(""))
//                .andExpect(status().isBadRequest());
//
//        verify(classService, never()).addClass(any(ClassRequest.class));
//    }
//
//    @Test
//    public void testUpdateClass_ShouldHandleNullRequestBody() throws Exception {
//        mockMvc.perform(put("/api/classes/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(""))
//                .andExpect(status().isBadRequest());
//
//        verify(classService, never()).updateClass(eq(1), any(ClassRequest.class));
//    }
//
//    @Test
//    public void testDeleteClass_ShouldHandleInvalidIdFormat() throws Exception {
//        mockMvc.perform(delete("/api/classes/invalid-id"))
//                .andExpect(status().isBadRequest());
//
//        verify(classService, never()).deleteClass(anyInt());
//    }
//
//    @Test
//    public void testHandleUnexpectedExceptions() throws Exception {
//        when(classService.findClassById(anyInt())).thenThrow(new RuntimeException("Unexpected error"));
//
//        mockMvc.perform(get("/api/classes/1"))
//                .andExpect(status().isInternalServerError());
////                .andExpect(jsonPath("$.message").value("Unexpected error"));
//
//        verify(classService, times(1)).findClassById(1);
//    }
//
//    @Test
//    public void testInvalidEndpoint_ShouldReturnNotFound() throws Exception {
//        mockMvc.perform(get("/api/invalid-endpoint"))
//                .andExpect(status().isNotFound());
//    }
//}
