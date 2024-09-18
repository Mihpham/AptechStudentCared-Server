package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.StudentRequest;
import com.example.aptechstudentcaredserver.bean.response.ImportResponse;
import com.example.aptechstudentcaredserver.bean.response.StudentResponse;
import com.example.aptechstudentcaredserver.enums.ClassMemberStatus;
import com.example.aptechstudentcaredserver.service.StudentService;
import com.example.aptechstudentcaredserver.util.ExcelUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/students")
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        List<StudentResponse> students = studentService.findAllStudent();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<StudentResponse>> getStudentsByStatus(@PathVariable("status") ClassMemberStatus status) {
        List<StudentResponse> students = studentService.findStudentsByStatus(status);
        return ResponseEntity.ok(students);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addStudent(@RequestBody StudentRequest studentRq) {
        try {
            studentService.createStudent(studentRq);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body("{\"message\": \"Student added successfully\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/import")
    public ResponseEntity<String> importStudents(@ModelAttribute("file") MultipartFile file) {
        try {
            List<ImportResponse> importResults = ExcelUtils.parseExcelFile(file,studentService);
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



    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponse> getStudentInfo(@PathVariable("studentId") int studentId) {
        StudentResponse studentResponse = studentService.findStudentById(studentId);
        return ResponseEntity.ok(studentResponse);
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable int studentId,
            @RequestBody StudentRequest studentRequest) {
        StudentResponse updatedStudent = studentService.updateStudent(studentId, studentRequest);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<String> deleteStudent(@PathVariable("studentId") int studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body("{\"message\": \"Student deleted successfully\"}");
    }

}
