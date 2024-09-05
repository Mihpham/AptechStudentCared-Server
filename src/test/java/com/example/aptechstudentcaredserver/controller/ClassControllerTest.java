package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
import com.example.aptechstudentcaredserver.service.ClassService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
class ClassControllerTest {

    @InjectMocks
    private ClassController classController;

    @Mock
    private ClassService classService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllClass_Success() {
        ClassResponse classResponse = new ClassResponse(1, "Class 1", "Center A", "10:00-12:00", "Mon-Wed", LocalDate.now(), "Active");
        List<ClassResponse> classResponses = Collections.singletonList(classResponse);

        when(classService.findAllClass()).thenReturn(classResponses);

        ResponseEntity<List<ClassResponse>> response = classController.findAllClass();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(classResponses, response.getBody());
    }

    @Test
    void testFindAllClass_Empty() {
        when(classService.findAllClass()).thenReturn(Collections.emptyList());

        ResponseEntity<List<ClassResponse>> response = classController.findAllClass();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }

    @Test
    void testFindClassById_Success() {
        int classId = 1;
        ClassResponse classResponse = new ClassResponse(1, "Class 1", "Center A", "10:00-12:00", "Mon-Wed", LocalDate.now(), "Active");

        when(classService.findClassById(classId)).thenReturn(classResponse);

        ResponseEntity<ClassResponse> response = classController.findClassById(classId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(classResponse, response.getBody());
    }

    @Test
    void testFindClassById_NotFound() {
        int classId = 1;
        when(classService.findClassById(classId)).thenThrow(new RuntimeException("Class not found"));

        ResponseEntity<ClassResponse> response = classController.findClassById(classId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAddClass_Success() {
        ClassRequest classRequest = new ClassRequest("Class 1", "Center A", "10:00-12:00", "Mon-Wed", LocalDate.now(), "Active");

        doNothing().when(classService).addClass(classRequest);

        ResponseEntity<String> response = classController.addClass(classRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Class added successfully", response.getBody());
    }

    @Test
    void testAddClass_Failure() {
        ClassRequest classRequest = new ClassRequest("Class 1", "Center A", "10:00-12:00", "Mon-Wed", LocalDate.now(), "Active");
        RuntimeException exception = new RuntimeException("Error adding class");

        doThrow(exception).when(classService).addClass(classRequest);

        ResponseEntity<String> response = classController.addClass(classRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error adding class", response.getBody());
    }

    @Test
    void testUpdateClass_Success() {
        int classId = 1;
        ClassRequest classRequest = new ClassRequest("Class 1", "Center A", "10:00-12:00", "Mon-Wed", LocalDate.now(), "Active");
        ClassResponse updatedClassResponse = new ClassResponse(1, "Class 1", "Center A", "10:00-12:00", "Mon-Wed", LocalDate.now(), "Active");

        when(classService.updateClass(classId, classRequest)).thenReturn(updatedClassResponse);

        ResponseEntity<ClassResponse> response = classController.updateClass(classRequest, classId);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(updatedClassResponse, response.getBody());
    }

    @Test
    void testUpdateClass_NotFound() {
        int classId = 1;
        ClassRequest classRequest = new ClassRequest("Class 1", "Center A", "10:00-12:00", "Mon-Wed", LocalDate.now(), "Active");
        when(classService.updateClass(classId, classRequest)).thenThrow(new RuntimeException("Class not found"));

        ResponseEntity<ClassResponse> response = classController.updateClass(classRequest, classId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteClass_Success() {
        int classId = 1;

        doNothing().when(classService).deleteClass(classId);

        ResponseEntity<String> response = classController.deleteClass(classId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delete successfully", response.getBody());
    }

    @Test
    void testDeleteClass_NotFound() {
        int classId = 1;
        doThrow(new RuntimeException("Class not found")).when(classService).deleteClass(classId);

        ResponseEntity<String> response = classController.deleteClass(classId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

