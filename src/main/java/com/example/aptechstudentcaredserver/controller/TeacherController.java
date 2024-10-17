package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.TeacherRequest;
import com.example.aptechstudentcaredserver.bean.response.TeacherResponse;
import com.example.aptechstudentcaredserver.service.TeacherService;
import com.example.aptechstudentcaredserver.service.impl.TeacherServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/teachers")
public class TeacherController {
    private final TeacherService teacherService;


    @PostMapping("/add")
    public ResponseEntity<String> registerTeacher(@RequestBody TeacherRequest teacherRq) {
        try {
            teacherService.registerTeacher(teacherRq);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body("{\"message\": \"Teacher added successfully\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<TeacherResponse>> getAllTeachers() {
        try {
            List<TeacherResponse> teachers = teacherService.findAllTeachers();
            return new ResponseEntity<>(teachers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<TeacherResponse>> getTeachersByClassId(@PathVariable("classId") int classId) {
        try {
            List<TeacherResponse> teachers = teacherService.findTeachersByClassId(classId);
            return new ResponseEntity<>(teachers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{teacherId}")
    public ResponseEntity<TeacherResponse> getTeacherById(@PathVariable("teacherId") int teacherId) {
        TeacherResponse teacherResponse = teacherService.findTeacherById(teacherId);
        return ResponseEntity.ok(teacherResponse);
    }

    @PutMapping("/{teacherId}")
    public ResponseEntity<TeacherResponse> updateTeacher(
            @PathVariable int teacherId,
            @RequestBody TeacherRequest teacherRequest) {
        TeacherResponse updatedTeacher = teacherService.updateTeacher(teacherRequest, teacherId);
        return ResponseEntity.ok(updatedTeacher);
    }

    @DeleteMapping("/{teacherId}")
    public ResponseEntity<String> deleteTeacher(@PathVariable("teacherId") int teacherId) {
        teacherService.deleteTeacher(teacherId);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body("{\"message\": \"Teacher deleted successfully\"}");
    }
}
