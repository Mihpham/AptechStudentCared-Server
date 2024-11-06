//package com.example.aptechstudentcaredserver.controller;
//
//import com.example.aptechstudentcaredserver.bean.request.StudentRequest;
//import com.example.aptechstudentcaredserver.bean.response.StudentResponse;
//import com.example.aptechstudentcaredserver.exception.NotFoundException;
//import com.example.aptechstudentcaredserver.service.StudentService;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class StudentControllerTest {
//
//    @Mock
//    private StudentService studentService;
//
//    @InjectMocks
//    private StudentController studentController;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // 1. Test retrieving all students
//    @Test
//    public void testGetAllStudents() {
//        List<StudentResponse> students = List.of(createStudentResponse("Nguyen Van A"), createStudentResponse("Tran Thi B"));
//
//        when(studentService.findAllStudent()).thenReturn(students);
//
//        ResponseEntity<List<StudentResponse>> response = studentController.getAllStudents();
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(2, response.getBody().size());
//        verify(studentService, times(1)).findAllStudent();
//    }
//
//    // 2. Test retrieving students when none exist
//    @Test
//    public void testGetAllStudentsEmpty() {
//        when(studentService.findAllStudent()).thenReturn(Collections.emptyList());
//
//        ResponseEntity<List<StudentResponse>> response = studentController.getAllStudents();
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertTrue(response.getBody().isEmpty());
//    }
//
//    // 3. Test retrieving a student by ID
//    @Test
//    public void testGetStudentById() {
//        StudentResponse studentResponse = createStudentResponse("Nguyen Van A");
//
//        when(studentService.findStudentById(1)).thenReturn(studentResponse);
//
//        ResponseEntity<StudentResponse> response = studentController.getStudentInfo(1);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Nguyen Van A", response.getBody().getFullName());
//    }
//
//    // 4. Test retrieving a student by ID not found
//    @Test
//    public void testGetStudentByIdNotFound() {
//        when(studentService.findStudentById(999)).thenThrow(new NotFoundException("Student not found"));
//
//        ResponseEntity<StudentResponse> response = studentController.getStudentInfo(999);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//    // 5. Test creating a student successfully
//    @Test
//    public void testCreateStudent() {
//        StudentRequest studentRequest = createValidStudentRequest();
//
//        ResponseEntity<String> response = studentController.addStudent(studentRequest);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("{\"message\": \"Student added successfully\"}", response.getBody());
//        verify(studentService, times(1)).createStudent(studentRequest);
//    }
//
//    // 6. Test creating a student with invalid data
////    @Test
////    public void testCreateStudentInvalidData() {
////        StudentRequest invalidRequest = new StudentRequest(); // Invalid data
////
////        ResponseEntity<String> response = studentController.addStudent(invalidRequest);
////
////        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
////        verify(studentService, never()).createStudent(any(StudentRequest.class));
////    }
//
//    // 7. Test updating a student successfully
//    @Test
//    public void testUpdateStudent() {
//        int studentId = 1;
//        StudentRequest studentRequest = createValidStudentRequest();
//        studentRequest.setFullName("Nguyen Van B");
//
//        StudentResponse updatedStudentResponse = createStudentResponse("Nguyen Van B");
//
//        when(studentService.updateStudent(studentId, studentRequest)).thenReturn(updatedStudentResponse);
//
//        ResponseEntity<StudentResponse> response = studentController.updateStudent(studentId, studentRequest);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Nguyen Van B", response.getBody().getFullName());
//        verify(studentService, times(1)).updateStudent(studentId, studentRequest);
//    }
//
//    // 8. Test updating a student that does not exist
//    @Test
//    public void testUpdateStudentNotFound() {
//        StudentRequest studentRequest = createValidStudentRequest();
//        when(studentService.updateStudent(anyInt(), any(StudentRequest.class)))
//                .thenThrow(new NotFoundException("Student not found"));
//        ResponseEntity<StudentResponse> response = studentController.updateStudent(999, studentRequest);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//    // 9. Test deleting a student successfully
//    @Test
//    public void testDeleteStudent() {
//        ResponseEntity<String> response = studentController.deleteStudent(1);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("{\"message\": \"Student deleted successfully\"}", response.getBody());
//        verify(studentService).deleteStudent(1);
//    }
//
//    // 10. Test deleting a student that does not exist
//    @Test
//    public void testDeleteStudentNotFound() {
//        doThrow(new NotFoundException("Student not found")).when(studentService).deleteStudent(999);
//
//        ResponseEntity<String> response = studentController.deleteStudent(999);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//    // 11. Test handling unexpected errors when retrieving a student by ID
//    @Test
//    public void testGetStudentByIdUnexpectedError() {
//        when(studentService.findStudentById(anyInt())).thenThrow(new RuntimeException("Unexpected error"));
//
//        ResponseEntity<StudentResponse> response = studentController.getStudentInfo(1);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//    }
//
//    // 12. Test creating a student with a null request body
////    @Test
////    public void testCreateStudentNullRequest() {
////        ResponseEntity<String> response = studentController.addStudent(null);
////
////        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
////        verify(studentService, never()).createStudent(any(StudentRequest.class));
////    }
//
//    // 13. Test updating a student with invalid data
////    @Test
////    public void testUpdateStudentInvalidData() {
////        StudentRequest invalidRequest = new StudentRequest();
////        ResponseEntity<StudentResponse> response = studentController.updateStudent(1, invalidRequest);
////
////        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
////        verify(studentService, never()).updateStudent(anyInt(), any(StudentRequest.class));
////    }
//
//    // 14. Test updating a student with a null request body
//    @Test
//    public void testUpdateStudentNullRequest() {
//        ResponseEntity<StudentResponse> response = studentController.updateStudent(1, null);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        verify(studentService, never()).updateStudent(anyInt(), any(StudentRequest.class));
//    }
//
//    // 15. Test retrieving a student by ID when service is empty
////    @Test
////    public void testGetStudentByIdServiceEmpty() {
////        when(studentService.findStudentById(1)).thenReturn(null);
////
////        ResponseEntity<StudentResponse> response = studentController.getStudentInfo(1);
////
////        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
////    }
//
//    // Helper method to create a valid StudentRequest
//    private StudentRequest createValidStudentRequest() {
//        StudentRequest studentRequest = new StudentRequest();
//        studentRequest.setFullName("Nguyen Van A");
//        studentRequest.setImage("image.jpg");
//        studentRequest.setPassword("password123");
//        studentRequest.setGender("Male");
//        studentRequest.setClassName("10A");
//        studentRequest.setDob("2000-01-01");
//        studentRequest.setPhoneNumber("1234567890");
//        studentRequest.setEmail("nguyenvana@example.com");
//        studentRequest.setAddress("123 Street");
//        studentRequest.setCourses(Set.of("Math", "English"));
//        studentRequest.setStatus("Active");
//        studentRequest.setParentFullName("Nguyen Thi C");
//        studentRequest.setStudentRelation("Mother");
//        studentRequest.setParentPhone("0987654321");
//        studentRequest.setParentGender("Female");
//        return studentRequest;
//    }
//
//    // Helper method to create a StudentResponse
//    private StudentResponse createStudentResponse(String fullName) {
//        StudentResponse studentResponse = new StudentResponse();
//        studentResponse.setFullName(fullName);
//        return studentResponse;
//    }
//}
