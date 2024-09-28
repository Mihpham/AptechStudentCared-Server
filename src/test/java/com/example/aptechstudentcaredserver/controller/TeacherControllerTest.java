package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.TeacherRequest;
import com.example.aptechstudentcaredserver.bean.response.TeacherResponse;
import com.example.aptechstudentcaredserver.service.TeacherService;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;

    @InjectMocks
    private TeacherController teacherController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // GET Requests

    @Test
    public void testGetAllTeachers_ShouldReturnListOfTeachers() {
        TeacherResponse teacherResponse1 = new TeacherResponse();
        teacherResponse1.setFullName("Nguyen Van A");

        TeacherResponse teacherResponse2 = new TeacherResponse();
        teacherResponse2.setFullName("Tran Thi B");

        when(teacherService.findAllTeachers()).thenReturn(List.of(teacherResponse1, teacherResponse2));

        ResponseEntity<List<TeacherResponse>> response = teacherController.getAllTeachers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(teacherService, times(1)).findAllTeachers();
    }

    @Test
    public void testGetAllTeachers_ShouldReturnEmptyList_WhenNoTeachers() {
        when(teacherService.findAllTeachers()).thenReturn(Collections.emptyList());

        ResponseEntity<List<TeacherResponse>> response = teacherController.getAllTeachers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(teacherService, times(1)).findAllTeachers();
    }

    @Test
    public void testGetTeacherById_ShouldReturnTeacher() {
        TeacherResponse teacherResponse = new TeacherResponse();
        teacherResponse.setFullName("Nguyen Van A");

        when(teacherService.findTeacherById(1)).thenReturn(teacherResponse);

        ResponseEntity<TeacherResponse> response = teacherController.getTeacherById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Nguyen Van A", response.getBody().getFullName());
        verify(teacherService, times(1)).findTeacherById(1);
    }

    @Test
    void testGetTeacherById_ShouldReturnNotFound_WhenTeacherDoesNotExist() {
        int nonExistentTeacherId = 999;
        when(teacherService.findTeacherById(nonExistentTeacherId)).thenThrow(new EntityNotFoundException("Teacher not found"));

        // Thực hiện yêu cầu
        ResponseEntity<TeacherResponse> response = teacherController.getTeacherById(nonExistentTeacherId);

        // Kiểm tra mã trạng thái
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    // POST Requests

    @Test
    public void testCreateTeacher_ShouldReturnCreatedTeacher() {
        TeacherRequest teacherRequest = new TeacherRequest();
        teacherRequest.setFullName("Nguyen Van A");
        teacherRequest.setPhoneNumber("1234567890");
        teacherRequest.setDob("1990-01-01");
        teacherRequest.setGender("Male");
        teacherRequest.setAddress("123 Street");
        teacherRequest.setStatus("Active");
        teacherRequest.setImage("image_url");

        ResponseEntity<String> response = teacherController.registerTeacher(teacherRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\": \"Teacher added successfully\"}", response.getBody());
        verify(teacherService, times(1)).registerTeacher(teacherRequest);
    }

    @Test
    public void testCreateTeacher_ShouldReturnBadRequest_WhenInvalidData() {
        TeacherRequest invalidRequest = new TeacherRequest(); // Dữ liệu không hợp lệ

        ResponseEntity<String> response = teacherController.registerTeacher(invalidRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(teacherService, never()).registerTeacher(any(TeacherRequest.class));
    }

    @Test
    public void testCreateTeacher_ShouldReturnBadRequest_WhenRequestBodyIsNull() {
        ResponseEntity<String> response = teacherController.registerTeacher(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(teacherService, never()).registerTeacher(any(TeacherRequest.class));
    }

    // PUT Requests

    @Test
    public void testUpdateTeacher_ShouldReturnUpdatedTeacher() {
        TeacherRequest teacherRequest = new TeacherRequest();
        teacherRequest.setFullName("Nguyen Van B");
        teacherRequest.setPhoneNumber("1234567890");
        teacherRequest.setDob("1990-01-01");
        teacherRequest.setGender("Male");
        teacherRequest.setAddress("123 Street");
        teacherRequest.setStatus("Active");
        teacherRequest.setImage("image_url");

        TeacherResponse updatedTeacherResponse = new TeacherResponse();
        updatedTeacherResponse.setFullName("Nguyen Van B");
        updatedTeacherResponse.setPhone("1234567890");
        updatedTeacherResponse.setDob("1990-01-01");
        updatedTeacherResponse.setGender("Male");
        updatedTeacherResponse.setAddress("123 Street");
        updatedTeacherResponse.setStatus("Active");
        updatedTeacherResponse.setImage("image_url");

        when(teacherService.updateTeacher(teacherRequest, 1)).thenReturn(updatedTeacherResponse);

        ResponseEntity<TeacherResponse> response = teacherController.updateTeacher(1, teacherRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Nguyen Van B", response.getBody().getFullName());
        verify(teacherService, times(1)).updateTeacher(teacherRequest, 1);
    }

    @Test
    public void testUpdateTeacher_ShouldReturnBadRequest_WhenInvalidData() {
        TeacherRequest invalidRequest = new TeacherRequest();
        ResponseEntity<TeacherResponse> response = teacherController.updateTeacher(1, invalidRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(teacherService, never()).updateTeacher(any(TeacherRequest.class), anyInt());
    }


    @Test
    public void testUpdateTeacher_ShouldHandleNullRequestBody() {
        ResponseEntity<TeacherResponse> response = teacherController.updateTeacher(1, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(teacherService, never()).updateTeacher(any(TeacherRequest.class), anyInt());
    }

    // DELETE Requests

    @Test
    public void testDeleteTeacher_ShouldReturnNoContent() {
        int teacherId = 1;
        TeacherResponse mockTeacher = new TeacherResponse(); // Giả lập một đối tượng TeacherResponse
        when(teacherService.findTeacherById(teacherId)).thenReturn(mockTeacher);

        ResponseEntity<String> response = teacherController.deleteTeacher(teacherId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\": \"Teacher deleted successfully\"}", response.getBody());
        // Xác minh rằng phương thức deleteTeacher của service đã được gọi
        verify(teacherService).deleteTeacher(teacherId);
    }





    // Edge Cases and Error Handling

    @Test
    void testUnexpectedErrorHandling() {
        // Giả lập một lỗi không mong đợi
        when(teacherService.findTeacherById(anyInt())).thenThrow(new RuntimeException("Unexpected error"));

        // Thực hiện yêu cầu
        ResponseEntity<TeacherResponse> response = teacherController.getTeacherById(1);

        // Kiểm tra mã trạng thái
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    @Test
    void testCreateTeacher_ShouldReturnBadRequest_WhenFullNameIsEmpty() {
        TeacherRequest request = new TeacherRequest();
        request.setFullName(""); // Tên rỗng
        request.setPhoneNumber("1234567890"); // Số điện thoại hợp lệ
        request.setDob("1990-01-01");
        request.setGender("Male");
        request.setStatus("Active");

        ResponseEntity<String> response = teacherController.registerTeacher(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateTeacher_ShouldReturnBadRequest_WhenPhoneNumberIsInvalid() {
        TeacherRequest request = new TeacherRequest();
        request.setFullName("John Doe");
        request.setPhoneNumber("abcde"); // Số điện thoại không hợp lệ
        request.setDob("1990-01-01");
        request.setGender("Male");
        request.setStatus("Active");

        ResponseEntity<String> response = teacherController.registerTeacher(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateTeacher_ShouldReturnBadRequest_WhenGenderIsInvalid() {
        TeacherRequest request = new TeacherRequest();
        request.setFullName("John Doe");
        request.setPhoneNumber("1234567890");
        request.setDob("1990-01-01");
        request.setGender("Unknown"); // Giới tính không hợp lệ
        request.setStatus("Active");

        ResponseEntity<String> response = teacherController.registerTeacher(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
