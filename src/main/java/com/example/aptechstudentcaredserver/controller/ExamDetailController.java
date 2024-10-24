package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.StudentExamScoreRequest;
import com.example.aptechstudentcaredserver.bean.response.ImportResponse;
import com.example.aptechstudentcaredserver.bean.response.StudentExamScoreResponse;
import com.example.aptechstudentcaredserver.service.ExamDetailService;
import com.example.aptechstudentcaredserver.util.ExcelUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/exam-score")
@RequiredArgsConstructor
public class ExamDetailController {
    private final ExamDetailService examDetailService;

    @GetMapping("/{classId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<List<StudentExamScoreResponse>> getExamScoresByClass(@PathVariable int classId) {
        List<StudentExamScoreResponse> examScores = examDetailService.getExamScoresByClass(classId);
        return ResponseEntity.ok(examScores);
    }

    @PutMapping("/update-score/{classId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO')")
    public ResponseEntity<StudentExamScoreResponse> updateStudentExamScore(
            @PathVariable int classId,
            @RequestBody StudentExamScoreRequest scoreRequest) {
        StudentExamScoreResponse updatedExamScore = examDetailService.updateStudentExamScore(scoreRequest, classId);
        return ResponseEntity.ok(updatedExamScore);
    }

    @PutMapping("/import/{classId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO')")
    public ResponseEntity<String> importStudents(
            @PathVariable int classId,
            @ModelAttribute("file") MultipartFile file) {
        try {
            List<ImportResponse> importResults = ExcelUtils.parseExamExcelFile(file, examDetailService, classId);
            StringBuilder errorMessage = new StringBuilder();

            for (ImportResponse result : importResults) {
                String message = result.getMessage();
                int rowNumber = result.getRowNumber();

                if (message.startsWith("Success")) {
                    continue; // Success messages are handled
                } else {
                    errorMessage.append("Row ").append(rowNumber).append(": ").append(message).append("\n");
                }
            }

            // If no errors recorded
            if (errorMessage.length() == 0) {
                return ResponseEntity.ok()
                        .body("{\"message\": \"All records processed successfully.\"}");
            } else {
                return ResponseEntity.badRequest().body(errorMessage.toString());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing file: " + e.getMessage());
        }
    }


}
