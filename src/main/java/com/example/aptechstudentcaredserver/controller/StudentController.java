package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.StudentRequest;
import com.example.aptechstudentcaredserver.bean.response.StudentResponse;
import com.example.aptechstudentcaredserver.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/students")
public class StudentController {
    private final StudentService studentService;


    @PostMapping("/add")
    public ResponseEntity<String> addStudent(@RequestBody StudentRequest studentRq) {
        try {
            studentService.createStudent(studentRq);
            return ResponseEntity.ok("Student added successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentInfo(@PathVariable("id") int id) {
        StudentResponse studentResponse = studentService.findStudentById(id);
        return ResponseEntity.ok(studentResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable("id") int id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

}
