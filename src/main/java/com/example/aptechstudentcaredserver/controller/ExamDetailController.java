package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.SubjectExamScoreRequest;
import com.example.aptechstudentcaredserver.bean.response.StudentExamScoreResponse;
import com.example.aptechstudentcaredserver.service.ExamDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-score")
@RequiredArgsConstructor
public class ExamDetailController {
    private final ExamDetailService examDetailService;

    @GetMapping("/{classId}")
    public ResponseEntity<List<StudentExamScoreResponse>> getExamScoresByClass(@PathVariable int classId) {
        List<StudentExamScoreResponse> examScores = examDetailService.getExamScoresByClass(classId);
        return ResponseEntity.ok(examScores);
    }

    @PutMapping("/update-score/{classId}")
    public ResponseEntity<StudentExamScoreResponse> updateStudentExamScore(
            @PathVariable int classId,
            @RequestBody SubjectExamScoreRequest scoreRequest) {
        StudentExamScoreResponse updatedExamScore = examDetailService.updateStudentExamScore(scoreRequest, classId);
        return ResponseEntity.ok(updatedExamScore);
    }

}
