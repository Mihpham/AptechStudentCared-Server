package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.response.ResponseMessage;
import com.example.aptechstudentcaredserver.bean.response.StudentPerformanceResponse;
import com.example.aptechstudentcaredserver.bean.response.SubjectPerformance;
import com.example.aptechstudentcaredserver.service.StudentPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/student-performance")
public class StudentPerformanceController {
    private final StudentPerformanceService saveStudentPerformance;

    @GetMapping("/class/{classId}/user/{userId}/subject/{subjectId}")
    public ResponseEntity<SubjectPerformance> saveStudentPerformance(
            @PathVariable int userId,
            @PathVariable int subjectId,
            @PathVariable int classId) {

        SubjectPerformance response = saveStudentPerformance.saveStudentPerformance(userId, subjectId, classId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("class/{classId}/user/{userId}/subjects")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> getAllSubjectsBySemester(
            @PathVariable int classId,
            @PathVariable int userId,
            @RequestParam(required = false) String semesterName) {
        try {
            StudentPerformanceResponse semesterSubjects = saveStudentPerformance.getAllSubjectsBySemester(classId, semesterName, userId);
            return new ResponseEntity<>(semesterSubjects, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}
