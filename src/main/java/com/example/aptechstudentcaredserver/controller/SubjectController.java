package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.SubjectRequest;
import com.example.aptechstudentcaredserver.bean.response.ImportResponse;
import com.example.aptechstudentcaredserver.bean.response.SubjectResponse;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.service.SubjectService;
import com.example.aptechstudentcaredserver.util.ExcelUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subjects")
public class SubjectController {
    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<List<SubjectResponse>> getAllSubjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<SubjectResponse> subjects = subjectService.findAllSubject(pageable);
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
        } catch (DuplicateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add subject.");
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

    @PostMapping("/import")
    public ResponseEntity<String> importSubjects(@ModelAttribute("file") MultipartFile file) {
        try {
            List<ImportResponse> importResults = ExcelUtils.parseSubjectExcelFile(file, subjectService);
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
    
