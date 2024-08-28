package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.StudentRequest;
import com.example.aptechstudentcaredserver.bean.response.StudentResponse;
import com.example.aptechstudentcaredserver.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/students")
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        List<StudentResponse> students = studentService.findAllStudent();
        return ResponseEntity.ok(students);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addStudent(@RequestBody StudentRequest studentRq) {
        try {
            studentService.createStudent(studentRq);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body("{\"message\": \"Student added successfully\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentInfo(@PathVariable("id") int id) {
        StudentResponse studentResponse = studentService.findStudentById(id);
        return ResponseEntity.ok(studentResponse);
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable int studentId,
            @RequestBody StudentRequest studentRequest) {
        StudentResponse updatedStudent = studentService.updateStudent(studentId, studentRequest);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable("id") int id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Student deleted successfully");
    }

}
