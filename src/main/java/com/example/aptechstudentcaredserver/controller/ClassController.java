package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.AssignTeacherRequest;
import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
import com.example.aptechstudentcaredserver.bean.response.ResponseMessage;
import com.example.aptechstudentcaredserver.service.ClassService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/classes")
public class ClassController {
    private final ClassService classService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ClassResponse>> findAllClass() {
        List<ClassResponse> classResponses = classService.findAllClass();
        return new ResponseEntity<>(classResponses, HttpStatus.OK);
    }

    @GetMapping("/{classId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ClassResponse> findClassById(@PathVariable int classId) {
        ClassResponse classResponse = classService.findClassById(classId);
        return new ResponseEntity<>(classResponse, HttpStatus.OK);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage> addClass(@Valid @RequestBody ClassRequest classRequest) {
        try {
            classService.addClass(classRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("Class added successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @PutMapping("/{classId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ClassResponse> updateClass(@RequestBody ClassRequest classRequest, @PathVariable int classId) {
        ClassResponse updateClass = classService.updateClass(classId, classRequest);
        return new ResponseEntity<>(updateClass, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{classId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage> deleteClass(@PathVariable int classId) {
        classService.deleteClass(classId);
        return new ResponseEntity<>(new ResponseMessage("Class deleted successfully"), HttpStatus.ACCEPTED);
    }

    @PutMapping("/{classId}/assign-teacher")
    public ResponseEntity<String> assignTeacherToSubject(
            @PathVariable int classId,
            @RequestBody AssignTeacherRequest request) {
        try {
            classService.assignTeacherToSubject(classId, request.getSubjectCode(), request.getTeacherName());
            return ResponseEntity.ok("Assign Teacher successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
