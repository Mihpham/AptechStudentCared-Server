package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.TeacherRequest;
import com.example.aptechstudentcaredserver.bean.response.TeacherResponse;
import com.example.aptechstudentcaredserver.service.TeacherService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
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
    public ResponseEntity<String> registerTeacher(@Valid @RequestBody TeacherRequest teacherRq) {
        try {
            // Kiểm tra nếu bất kỳ trường nào là null
            if (teacherRq.getFullName() == null ||
                    teacherRq.getPhoneNumber() == null ||
                    teacherRq.getDob() == null ||
                    teacherRq.getGender() == null ||
                    teacherRq.getAddress() == null ||
                    teacherRq.getStatus() == null) {   // Kiểm tra thêm trường status
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"One or more fields are missing\"}");
            }

            teacherService.registerTeacher(teacherRq);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body("{\"message\": \"Teacher added successfully\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"An unexpected error occurred\"}");
        }
    }



    @GetMapping
    public ResponseEntity<List<TeacherResponse>> getAllTeachers() {
        try {
            List<TeacherResponse> teachers = teacherService.findAllTeachers();
            return new ResponseEntity<>(teachers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{teacherId}")
    public ResponseEntity<TeacherResponse> getTeacherById(@PathVariable("teacherId") int teacherId) {
        try {
            TeacherResponse teacherResponse = teacherService.findTeacherById(teacherId);
            return ResponseEntity.ok(teacherResponse);
        } catch (EntityNotFoundException e) {
            // Nếu giáo viên không tồn tại, trả về 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (RuntimeException e) {
            // Đối với các lỗi không mong đợi khác, trả về 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    @PutMapping("/{teacherId}")
    public ResponseEntity<TeacherResponse> updateTeacher(
            @PathVariable int teacherId,
            @Valid @RequestBody TeacherRequest teacherRequest) {
        try {
            if (teacherRequest.getFullName() == null ||
                    teacherRequest.getPhoneNumber() == null ||
                    teacherRequest.getDob() == null ||
                    teacherRequest.getGender() == null ||
                    teacherRequest.getAddress() == null ||
                    teacherRequest.getStatus() == null) {
                throw new BadRequestException("One or more fields are missing");
            }

            TeacherResponse updatedTeacher = teacherService.updateTeacher(teacherRequest, teacherId);
            return ResponseEntity.ok(updatedTeacher);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{teacherId}")
    public ResponseEntity<String> deleteTeacher(@PathVariable int teacherId) {
        try {
            TeacherResponse teacher = teacherService.findTeacherById(teacherId);
            if (teacher == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"Teacher not found\"}");
            }
            teacherService.deleteTeacher(teacherId);
            return ResponseEntity.ok("{\"message\": \"Teacher deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"An unexpected error occurred\"}");
        }
    }


}
