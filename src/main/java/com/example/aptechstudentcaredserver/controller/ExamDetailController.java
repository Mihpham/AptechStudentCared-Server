package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.SubjectExamScoreRequest;
import com.example.aptechstudentcaredserver.bean.response.ImportResponse;
import com.example.aptechstudentcaredserver.bean.response.StudentExamScoreResponse;
import com.example.aptechstudentcaredserver.service.ExamDetailService;
import com.example.aptechstudentcaredserver.util.ExcelUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/import")
    public ResponseEntity<String> importStudents(@ModelAttribute("file") MultipartFile file) {
        try {
            List<ImportResponse> importResults = ExcelUtils.parseExamExcelFile(file,examDetailService);
            StringBuilder errorMessage = new StringBuilder();

            for (ImportResponse result : importResults) {
                String message = result.getMessage();
                int rowNumber = result.getRowNumber();

                if (message.startsWith("Success")) {
                    // Success messages are handled by the importResults itself
                    continue;
                } else {
                    // Append error details including the row number
                    errorMessage.append("Row ").append(rowNumber).append(": ").append(message).append("\n");
                }
            }

            // Nếu không có lỗi nào được ghi lại, trả về thông báo thành công toàn bộ
            if (errorMessage.length() == 0) {
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .body("{\"message\": \"All records in the file were processed successfully without any errors.\"}");
            } else {
                // Nếu có lỗi, trả về thông báo lỗi
                return ResponseEntity.badRequest().body(errorMessage.toString());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing file: " + e.getMessage());
        }
    }

}
