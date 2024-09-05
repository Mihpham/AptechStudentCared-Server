package com.example.aptechstudentcaredserver.controller;


import com.example.aptechstudentcaredserver.bean.request.StudentRequest;
import com.example.aptechstudentcaredserver.bean.response.StudentResponse;
import com.example.aptechstudentcaredserver.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {


    @InjectMocks
    private StudentController studentController;

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private StudentService studentService;

        private StudentResponse studentResponse;
        private List<StudentResponse> studentResponses;

        /**
         * Thiết lập dữ liệu mẫu trước khi mỗi bài kiểm tra chạy.
         * <p>
         * Tạo các đối tượng StudentResponse mẫu và danh sách các đối tượng này để sử dụng trong các bài kiểm tra.
         * </p>
         */
        @BeforeEach
        void setUp() {
            LocalDateTime now = LocalDateTime.now();

            StudentResponse student1 = new StudentResponse();
            student1.setUserId(1);
            student1.setFullName("John Doe");
            student1.setRollNumber("12345");
            student1.setGender("Male");
            student1.setClassName("Class A");
//            student1.set(now.toLocalDate());
            student1.setPhoneNumber("1234567890");
            student1.setEmail("john.doe@example.com");
            student1.setAddress("123 Main St");
            student1.setCourses(Arrays.asList("Math", "Science"));
            student1.setStatus("Active");
            student1.setParentFullName("Jane Doe");
            student1.setStudentRelation("Mother");
            student1.setParentPhone("0987654321");
            student1.setParentGender("Female");

            StudentResponse student2 = new StudentResponse();
            student2.setUserId(2);
            student2.setFullName("Jane Smith");
            student2.setRollNumber("67890");
            student2.setGender("Female");
            student2.setClassName("Class B");
//            student2.setDob(now.toLocalDate());
            student2.setPhoneNumber("9876543210");
            student2.setEmail("jane.smith@example.com");
            student2.setAddress("456 Elm St");
            student2.setCourses(Arrays.asList("English", "History"));
            student2.setStatus("Inactive");
            student2.setParentFullName("John Smith");
            student2.setStudentRelation("Father");
            student2.setParentPhone("0123456789");
            student2.setParentGender("Male");

            studentResponse = student1;
            studentResponses = Arrays.asList(student1, student2);
        }

        /**
         * Kiểm tra phương thức GET /api/students với các sinh viên có sẵn.
         * <p>
         * Giả lập hành vi của studentService.findAllStudents() để trả về danh sách các sinh viên mẫu và kiểm tra phản hồi HTTP.
         * </p>
         *
         * @throws Exception nếu có lỗi xảy ra khi thực hiện yêu cầu HTTP.
         */
        @Test
        @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
        void getAllStudents_success() throws Exception {
            Mockito.when(studentService.findAllStudent()).thenReturn(studentResponses);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/students")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(studentResponses.size()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].fullName").value("John Doe"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].rollNumber").value("12345"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].gender").value("Male"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].className").value("Class A"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].fullName").value("Jane Smith"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].rollNumber").value("67890"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].gender").value("Female"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].className").value("Class B"));
        }
    // Test case cho API addStudent với dữ liệu hợp lệ
    @Test
    void addStudent_validRequest_shouldReturnSuccessMessage() throws Exception {
        StudentRequest studentRequest = StudentRequest.builder()
                .image("image.png")
                .rollNumber("S001")
                .fullName("John Doe")
                .password("password")
                .gender("Male")
                .className("Class 1")
                .dob("2000-01-01")
                .phoneNumber("1234567890")
                .email("john@example.com")
                .address("123 Street")
                .courses(Set.of("Math", "Science"))
                .status("Active")
                .parentFullName("Jane Doe")
                .studentRelation("Mother")
                .parentPhone("0987654321")
                .parentGender("Female")
                .build();

        mockMvc.perform(post("/api/students/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"rollNumber\": \"S001\", \"fullName\": \"John Doe\", \"email\": \"john@example.com\", \"phoneNumber\": \"1234567890\", \"password\": \"password\" }"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Student added successfully"));

        verify(studentService, times(1)).createStudent(any(StudentRequest.class));
    }

    @Test
    void addStudent_missingField_shouldReturnBadRequest() throws Exception {
        // Thiếu trường fullName
        mockMvc.perform(post("/api/students/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"rollNumber\": \"S001\", \"email\": \"john@example.com\", \"phoneNumber\": \"1234567890\", \"gender\": \"Male\", \"className\": \"Class 1\" }"))
                .andExpect(status().isBadRequest());

        // Thiếu trường rollNumber
        mockMvc.perform(post("/api/students/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"fullName\": \"John Doe\", \"email\": \"john@example.com\", \"phoneNumber\": \"1234567890\", \"gender\": \"Male\", \"className\": \"Class 1\" }"))
                .andExpect(status().isBadRequest());

        // Thiếu trường email
        mockMvc.perform(post("/api/students/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"fullName\": \"John Doe\", \"rollNumber\": \"S001\", \"phoneNumber\": \"1234567890\", \"gender\": \"Male\", \"className\": \"Class 1\" }"))
                .andExpect(status().isBadRequest());

        // Thiếu trường phoneNumber
        mockMvc.perform(post("/api/students/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"fullName\": \"John Doe\", \"rollNumber\": \"S001\", \"email\": \"john@example.com\", \"gender\": \"Male\", \"className\": \"Class 1\" }"))
                .andExpect(status().isBadRequest());

        // Thiếu trường gender
        mockMvc.perform(post("/api/students/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"fullName\": \"John Doe\", \"rollNumber\": \"S001\", \"email\": \"john@example.com\", \"phoneNumber\": \"1234567890\", \"className\": \"Class 1\" }"))
                .andExpect(status().isBadRequest());

        // Thiếu trường className
        mockMvc.perform(post("/api/students/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"fullName\": \"John Doe\", \"rollNumber\": \"S001\", \"email\": \"john@example.com\", \"phoneNumber\": \"1234567890\", \"gender\": \"Male\" }"))
                .andExpect(status().isBadRequest());

        // Verify that studentService.createStudent is never called
        verify(studentService, never()).createStudent(any(StudentRequest.class));
    }


    // Test case cho API getStudentInfo với ID hợp lệ
    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void getStudentInfo_existingStudentId_shouldReturnStudent() throws Exception {
        // Tạo một đối tượng StudentResponse giả định với đầy đủ thông tin
        StudentResponse studentResponse = new StudentResponse(1, "image1.png", "S001", "John Doe",
                "john@example.com", "123 Street", "Class 1", "Male", "01/01/2000",
                "1234567890", Arrays.asList("Math", "Science"), "Active",
                "Jane Doe", "Mother", "0987654321", "Female");

        // Khi gọi đến studentService.findStudentById(1), trả về đối tượng studentResponse
        when(studentService.findStudentById(1)).thenReturn(studentResponse);

        // Kiểm tra request và phản hồi
        mockMvc.perform(get("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.image").value("image1.png"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rollNumber").value("S001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value("John Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("john@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("123 Street"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.className").value("Class 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gender").value("Male"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dob").value("01/01/2000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phoneNumber").value("1234567890"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses[0]").value("Math"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses[1]").value("Science"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Active"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parentFullName").value("Jane Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.studentRelation").value("Mother"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parentPhone").value("0987654321"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parentGender").value("Female"));
    }


    // Test case cho API getStudentInfo với ID không tồn tại
    @Test
    void getStudentInfo_nonExistingStudentId_shouldReturnNotFound() throws Exception {
        when(studentService.findStudentById(999)).thenThrow(new RuntimeException("Student not found"));

        mockMvc.perform(get("/api/students/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // Test case cho API updateStudent với ID hợp lệ
    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void updateStudent_existingStudentId_shouldUpdateAndReturnStudent() throws Exception {
        // Create a StudentRequest object with update information
        StudentRequest studentRequest = StudentRequest.builder()
                .image("image.png")
                .rollNumber("S001")
                .fullName("John Doe Updated")
                .password("password")
                .gender("Male")
                .className("Class 1")
                .dob("2000-01-01")
                .phoneNumber("1234567890")
                .email("john@example.com")
                .address("123 Street Updated")
                .courses(Set.of("Math", "Science"))
                .status("Active")
                .parentFullName("Jane Doe")
                .studentRelation("Mother")
                .parentPhone("0987654321")
                .parentGender("Female")
                .build();

        // Create a StudentResponse object with updated information
        StudentResponse studentResponse = new StudentResponse(1, "image.png", "S001", "John Doe Updated",
                "john@example.com", "123 Street Updated", "Class 1", "Male", "2000-01-01",
                "1234567890", Arrays.asList("Math", "Science"), "Active",
                "Jane Doe", "Mother", "0987654321", "Female");

        // Mock the updateStudent method
        when(studentService.updateStudent(eq(1), any(StudentRequest.class))).thenReturn(studentResponse);

        // Perform PUT request and check all fields in the response
        mockMvc.perform(put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"image\": \"image.png\","
                                + "\"rollNumber\": \"S001\","
                                + "\"fullName\": \"John Doe Updated\","
                                + "\"password\": \"password\","
                                + "\"gender\": \"Male\","
                                + "\"className\": \"Class 1\","
                                + "\"dob\": \"2000-01-01\","
                                + "\"phoneNumber\": \"1234567890\","
                                + "\"email\": \"john@example.com\","
                                + "\"address\": \"123 Street Updated\","
                                + "\"courses\": [\"Math\", \"Science\"],"
                                + "\"status\": \"Active\","
                                + "\"parentFullName\": \"Jane Doe\","
                                + "\"studentRelation\": \"Mother\","
                                + "\"parentPhone\": \"0987654321\","
                                + "\"parentGender\": \"Female\""
                                + "}"))
                .andExpect(status().isOk()) // Check HTTP 200 response
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.image").value("image.png"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rollNumber").value("S001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value("John Doe Updated"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("john@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("123 Street Updated"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phoneNumber").value("1234567890"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dob").value("2000-01-01"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gender").value("Male"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.className").value("Class 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses[0]").value("Math"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses[1]").value("Science"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Active"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parentFullName").value("Jane Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.studentRelation").value("Mother"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parentPhone").value("0987654321"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parentGender").value("Female"));

        // Verify that updateStudent() was called exactly once
        verify(studentService, times(1)).updateStudent(eq(1), any(StudentRequest.class));
    }


    // Test case cho API updateStudent với ID không tồn tại
    @Test
    void updateStudent_nonExistingStudentId_shouldReturnNotFound() throws Exception {
        when(studentService.updateStudent(eq(999), any(StudentRequest.class))).thenThrow(new RuntimeException("Student not found"));

        mockMvc.perform(put("/api/students/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"rollNumber\": \"S001\", \"fullName\": \"John Doe Updated\", \"email\": \"john@example.com\", \"phoneNumber\": \"1234567890\" }"))
                .andExpect(status().isNotFound());
    }

    // Test case cho API deleteStudent với ID hợp lệ
    @Test
    void deleteStudent_existingStudentId_shouldReturnSuccessMessage() throws Exception {
        doNothing().when(studentService).deleteStudent(1);

        mockMvc.perform(delete("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Student deleted successfully"));

        verify(studentService, times(1)).deleteStudent(1);
    }

    // Test case cho API deleteStudent với ID không tồn tại
    @Test
    void deleteStudent_nonExistingStudentId_shouldReturnNotFound() throws Exception {
        doThrow(new RuntimeException("Student not found")).when(studentService).deleteStudent(999);

        mockMvc.perform(delete("/api/students/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}


