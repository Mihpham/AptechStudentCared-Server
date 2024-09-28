//package com.example.aptechstudentcaredserver.controller;
//
//import com.example.aptechstudentcaredserver.bean.request.StudentRequest;
//import com.example.aptechstudentcaredserver.bean.response.StudentResponse;
//import com.example.aptechstudentcaredserver.service.StudentService;
//import com.example.aptechstudentcaredserver.exception.NotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Set;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class StudentControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private StudentService studentService;
//
//    private StudentResponse studentResponse;
//    private List<StudentResponse> studentResponses;
//
//    @BeforeEach
//    void setUp() {
//        StudentResponse student1 = new StudentResponse();
//        student1.setUserId(1);
//        student1.setFullName("John Doe");
//        student1.setRollNumber("12345");
//        student1.setGender("Male");
//        student1.setClassName("Class A");
//        student1.setPhoneNumber("1234567890");
//        student1.setEmail("john.doe@example.com");
//        student1.setAddress("123 Main St");
//        student1.setCourses(Arrays.asList("Math", "Science"));
//        student1.setStatus("Active");
//        student1.setParentFullName("Jane Doe");
//        student1.setStudentRelation("Mother");
//        student1.setParentPhone("0987654321");
//        student1.setParentGender("Female");
//
//        StudentResponse student2 = new StudentResponse();
//        student2.setUserId(2);
//        student2.setFullName("Jane Smith");
//        student2.setRollNumber("67890");
//        student2.setGender("Female");
//        student2.setClassName("Class B");
//        student2.setPhoneNumber("9876543210");
//        student2.setEmail("jane.smith@example.com");
//        student2.setAddress("456 Elm St");
//        student2.setCourses(Arrays.asList("English", "History"));
//        student2.setStatus("Inactive");
//        student2.setParentFullName("John Smith");
//        student2.setStudentRelation("Father");
//        student2.setParentPhone("0123456789");
//        student2.setParentGender("Male");
//
//        studentResponse = student1;
//        studentResponses = Arrays.asList(student1, student2);
//    }
//
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    void getAllStudents_success() throws Exception {
//        when(studentService.findAllStudent()).thenReturn(studentResponses);
//
//        mockMvc.perform(get("/api/students")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(studentResponses.size()))
//                .andExpect(jsonPath("$[0].userId").value(1))
//                .andExpect(jsonPath("$[0].fullName").value("John Doe"))
//                .andExpect(jsonPath("$[0].rollNumber").value("12345"))
//                .andExpect(jsonPath("$[0].gender").value("Male"))
//                .andExpect(jsonPath("$[0].className").value("Class A"))
//                .andExpect(jsonPath("$[1].userId").value(2))
//                .andExpect(jsonPath("$[1].fullName").value("Jane Smith"))
//                .andExpect(jsonPath("$[1].rollNumber").value("67890"))
//                .andExpect(jsonPath("$[1].gender").value("Female"))
//                .andExpect(jsonPath("$[1].className").value("Class B"));
//    }
//
////    @Test
////    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
////    void addStudent_validRequest_shouldReturnSuccessMessage() throws Exception {
////        StudentRequest studentRequest = StudentRequest.builder()
////                .image("image.png")
////                .rollNumber("S001")
////                .fullName("John Doe")
////                .password("password")
////                .gender("Male")
////                .className("Class 1")
////                .dob("2000-01-01")
////                .phoneNumber("1234567890")
////                .email("john@example.com")
////                .address("123 Street")
////                .courses(Set.of("Math", "Science"))
////                .status("Active")
////                .parentFullName("Jane Doe")
////                .studentRelation("Mother")
////                .parentPhone("0987654321")
////                .parentGender("Female")
////                .build();
////
////        mockMvc.perform(post("/api/students/add")
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(String.valueOf(studentRequest)))
////                .andExpect(status().isOk())
////                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
////                .andExpect(jsonPath("$.message").value("Student added successfully"));
////
////        verify(studentService, times(1)).createStudent(any(StudentRequest.class));
////    }
//
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    void addStudent_missingField_shouldReturnBadRequest() throws Exception {
//        // Missing fullName
//        mockMvc.perform(post("/api/students/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ \"rollNumber\": \"S001\", \"email\": \"john@example.com\", \"phoneNumber\": \"1234567890\", \"gender\": \"Male\", \"className\": \"Class 1\" }"))
//                .andExpect(status().isBadRequest());
//
//        // Missing rollNumber
//        mockMvc.perform(post("/api/students/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ \"fullName\": \"John Doe\", \"email\": \"john@example.com\", \"phoneNumber\": \"1234567890\", \"gender\": \"Male\", \"className\": \"Class 1\" }"))
//                .andExpect(status().isBadRequest());
//
//        // Missing email
//        mockMvc.perform(post("/api/students/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ \"fullName\": \"John Doe\", \"rollNumber\": \"S001\", \"phoneNumber\": \"1234567890\", \"gender\": \"Male\", \"className\": \"Class 1\" }"))
//                .andExpect(status().isBadRequest());
//
//        // Missing phoneNumber
//        mockMvc.perform(post("/api/students/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ \"fullName\": \"John Doe\", \"rollNumber\": \"S001\", \"email\": \"john@example.com\", \"gender\": \"Male\", \"className\": \"Class 1\" }"))
//                .andExpect(status().isBadRequest());
//
//        // Missing gender
//        mockMvc.perform(post("/api/students/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ \"fullName\": \"John Doe\", \"rollNumber\": \"S001\", \"email\": \"john@example.com\", \"phoneNumber\": \"1234567890\", \"className\": \"Class 1\" }"))
//                .andExpect(status().isBadRequest());
//
//        // Missing className
//        mockMvc.perform(post("/api/students/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ \"fullName\": \"John Doe\", \"rollNumber\": \"S001\", \"email\": \"john@example.com\", \"phoneNumber\": \"1234567890\", \"gender\": \"Male\" }"))
//                .andExpect(status().isBadRequest());
//
//        // Verify that studentService.createStudent is never called
//        verify(studentService, never()).createStudent(any(StudentRequest.class));
//    }
//
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    void getStudentInfo_existingStudentId_shouldReturnStudent() throws Exception {
//        // Create a StudentResponse object with all required fields
//        StudentResponse studentResponse = new StudentResponse(
//                1,                       // userId
//                "image1.png",            // image
//                "S001",                  // rollNumber
//                "John Doe",              // fullName
//                "john@example.com",     // email
//                "123 Street",            // address
//                "Class 1",               // className
//                "Male",                  // gender
//                "2000-01-01",            // dob
//                "1234567890",            // phoneNumber
//                List.of("Math", "Science"), // courses
//                "Active",                // status
//                "Jane Doe",              // parentFullName
//                "Mother",                // studentRelation
//                "0987654321",            // parentPhone
//                "Female"                 // parentGender
//        );
//
//        // Mock the service call
//        when(studentService.findStudentById(1)).thenReturn(studentResponse);
//
//        // Perform the GET request and assert the response
//        mockMvc.perform(get("/api/students/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.fullName").value("John Doe"))
//                .andExpect(jsonPath("$.rollNumber").value("S001"))
//                .andExpect(jsonPath("$.email").value("john@example.com"))
//                .andExpect(jsonPath("$.address").value("123 Street"))
//                .andExpect(jsonPath("$.gender").value("Male"))
//                .andExpect(jsonPath("$.className").value("Class 1"))
//                .andExpect(jsonPath("$.dob").value("2000-01-01"))
//                .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
//                .andExpect(jsonPath("$.courses[0]").value("Math"))
//                .andExpect(jsonPath("$.courses[1]").value("Science"))
//                .andExpect(jsonPath("$.status").value("Active"))
//                .andExpect(jsonPath("$.parentFullName").value("Jane Doe"))
//                .andExpect(jsonPath("$.studentRelation").value("Mother"))
//                .andExpect(jsonPath("$.parentPhone").value("0987654321"))
//                .andExpect(jsonPath("$.parentGender").value("Female"));
//    }
//
//
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    void getStudentInfo_nonExistingStudentId_shouldReturnNotFound() throws Exception {
//        when(studentService.findStudentById(999)).thenThrow(new NotFoundException("Student not found"));
//
//        mockMvc.perform(get("/api/students/999")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
////    @Test
////    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
////    void updateStudent_existingStudentId_shouldReturnSuccessMessage() throws Exception {
////        StudentRequest studentRequest = StudentRequest.builder()
////                .image("updated_image.png")
////                .rollNumber("S001")
////                .fullName("John Doe Updated")
////                .password("new_password")
////                .gender("Male")
////                .className("Class 2")
////                .dob("2000-01-01")
////                .phoneNumber("0987654321")
////                .email("john.updated@example.com")
////                .address("789 New St")
////                .courses(Set.of("Physics", "Biology"))
////                .status("Active")
////                .parentFullName("Jane Doe")
////                .studentRelation("Mother")
////                .parentPhone("1234567890")
////                .parentGender("Female")
////                .build();
////
////        doNothing().when(studentService).updateStudent(anyInt(), any(StudentRequest.class));
////
////        mockMvc.perform(put("/api/students/1")
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content("{ \"fullName\": \"John Doe Updated\", \"email\": \"john.updated@example.com\", \"phoneNumber\": \"0987654321\", \"gender\": \"Male\", \"className\": \"Class 2\" }"))
////                .andExpect(status().isOk())
////                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
////                .andExpect(jsonPath("$.message").value("Student updated successfully"));
////
////        verify(studentService, times(1)).updateStudent(anyInt(), any(StudentRequest.class));
////    }
//
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    void updateStudent_nonExistingStudentId_shouldReturnNotFound() throws Exception {
//        StudentRequest studentRequest = StudentRequest.builder()
//                .image("updated_image.png")
//                .rollNumber("S001")
//                .fullName("John Doe Updated")
//                .password("new_password")
//                .gender("Male")
//                .className("Class 2")
//                .dob("2000-01-01")
//                .phoneNumber("0987654321")
//                .email("john.updated@example.com")
//                .address("789 New St")
//                .courses(Set.of("Physics", "Biology"))
//                .status("Active")
//                .parentFullName("Jane Doe")
//                .studentRelation("Mother")
//                .parentPhone("1234567890")
//                .parentGender("Female")
//                .build();
//
//        doThrow(new NotFoundException("Student not found")).when(studentService).updateStudent(anyInt(), any(StudentRequest.class));
//
//        mockMvc.perform(put("/api/students/999")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ \"fullName\": \"John Doe Updated\", \"email\": \"john.updated@example.com\", \"phoneNumber\": \"0987654321\", \"gender\": \"Male\", \"className\": \"Class 2\" }"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    void deleteStudent_existingStudentId_shouldReturnSuccessMessage() throws Exception {
//        doNothing().when(studentService).deleteStudent(anyInt());
//
//        mockMvc.perform(delete("/api/students/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isAccepted());
////                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
////                .andExpect(jsonPath("$.message").value("Student deleted successfully"));
//
//        verify(studentService, times(1)).deleteStudent(anyInt());
//    }
//
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    void deleteStudent_nonExistingStudentId_shouldReturnNotFound() throws Exception {
//        doThrow(new NotFoundException("Student not found")).when(studentService).deleteStudent(anyInt());
//
//        mockMvc.perform(delete("/api/students/999")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//}
