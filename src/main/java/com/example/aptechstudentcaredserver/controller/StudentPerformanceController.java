package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.response.StudentPerformanceResponse;
import com.example.aptechstudentcaredserver.service.StudentPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/student-performance")
public class StudentPerformanceController {
    private final StudentPerformanceService saveStudentPerformance;

    @GetMapping("/class/{classId}/user/{userId}/subject/{subjectId}")
    public ResponseEntity<StudentPerformanceResponse> saveStudentPerformance(
            @PathVariable int userId,
            @PathVariable int subjectId,
            @PathVariable int classId) {

        StudentPerformanceResponse response = saveStudentPerformance.saveStudentPerformance(userId, subjectId, classId);
        return ResponseEntity.ok(response);
    }
}
