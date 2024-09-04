package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.SubjectRequest;
import com.example.aptechstudentcaredserver.bean.response.SubjectResponse;
import com.example.aptechstudentcaredserver.bean.response.SubjectResponse;
import com.example.aptechstudentcaredserver.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subjects")
public class SubjectController {
        private final SubjectService subjectService;

        @GetMapping
        public ResponseEntity<List<SubjectResponse>> getAllSubjects() {
            List<SubjectResponse> subjects = subjectService.findAllSubject();
            return ResponseEntity.ok(subjects);
        }
        @GetMapping("/{id}")
        public ResponseEntity<SubjectResponse> getSubjectById(@PathVariable int id) {
            try {
                SubjectResponse subjectResponse = subjectService.findSubjectById(id);
                return ResponseEntity.ok(subjectResponse);
            } catch (RuntimeException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found", e);
            }
        }
        @PostMapping("/add")
        public ResponseEntity<String> addSubject(@Valid @RequestBody SubjectRequest subjectRq) {
            try {
                subjectService.createSubject(subjectRq);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .body("{\"message\": \"Subject added successfully\"}");
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        @PutMapping("/{subjectId}")
        public ResponseEntity<SubjectResponse> updateSubject(
                @PathVariable int subjectId,
                @RequestBody SubjectRequest subjectRequest) {
            SubjectResponse updatedSubject = subjectService.updateSubject(subjectId, subjectRequest);
            return ResponseEntity.ok(updatedSubject);
        }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable int id) {
        try {
            subjectService.deleteSubject(id);
            return ResponseEntity.ok(Map.of("message", "Subject deleted successfully"));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    }
    
