package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.AssignTeacherRequest;
import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassDetailResponse;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
import com.example.aptechstudentcaredserver.bean.response.CourseWithClassesResponse;
import com.example.aptechstudentcaredserver.bean.response.ResponseMessage;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.enums.Status;
import com.example.aptechstudentcaredserver.service.ClassService;
import com.example.aptechstudentcaredserver.service.impl.ClassServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/classes")
public class ClassController {
    private final ClassService classService;
    private final ClassServiceImpl classServiceImpl;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<Page<ClassResponse>> findAllClass(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<ClassResponse> classResponses = classService.findAllClass(pageable);

        return ResponseEntity.ok(classResponses);
    }

    @GetMapping("/semester")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<Map<String, Long>> getClassBySemester() {
        // Gọi service để lấy số lượng lớp theo học kỳ
        Map<String, Long> classCountBySemester = classService.findClassCountBySemester();

        // Trả về kết quả
        return ResponseEntity.ok(classCountBySemester);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ClassResponse>> searchClass(
            @RequestParam(required = false) String className,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ClassResponse> classResponses = classService.searchClass(className, pageable);

        return ResponseEntity.ok(classResponses);
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO') or hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<CourseWithClassesResponse> findClassWithSubjectByClassId(@PathVariable int classId) {
        CourseWithClassesResponse classDetails = classService.findClassWithSubjectByClassId(classId);
        return new ResponseEntity<>(classDetails, HttpStatus.OK);
    }

    @GetMapping("/{classId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<ClassDetailResponse> findClassById(
            @PathVariable int classId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        ClassDetailResponse classResponse = classService.findClassById(classId, pageable);
        return new ResponseEntity<>(classResponse, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<Page<ClassResponse>> findClassByStatus(
            @PathVariable("status") Status status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ClassResponse> classResponse = classService.findClassByStatus(status, pageable);
        return ResponseEntity.ok(classResponse);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO')")
    public ResponseEntity<ResponseMessage> addClass(@Valid @RequestBody ClassRequest classRequest) {
        try {
            classService.addClass(classRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("Class added successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @PutMapping("/{classId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO')")
    public ResponseEntity<ClassResponse> updateClass(@RequestBody ClassRequest classRequest, @PathVariable int classId) {
        ClassResponse updateClass = classService.updateClass(classId, classRequest);
        return new ResponseEntity<>(updateClass, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{classId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO')")
    public ResponseEntity<ResponseMessage> deleteClass(@PathVariable int classId) {
        classService.deleteClass(classId);
        return new ResponseEntity<>(new ResponseMessage("Class deleted successfully"), HttpStatus.ACCEPTED);
    }

    @PutMapping("/{classId}/assign-teacher")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO')")
    public ResponseEntity<String> assignTeacherToSubject(
            @PathVariable int classId,
            @RequestBody AssignTeacherRequest request) {
        try {
            classService.assignTeacherToSubject(classId, request);
            return ResponseEntity.ok("Assign Teacher successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO') or hasRole('ROLE_TEACHER')")
    public List<ClassResponse> getClassesByUser(@PathVariable int userId) {
        User user = new User();  // Load user theo userId (có thể lấy từ UserService hoặc repository)
        user.setId(userId);      // Đặt user ID
        return classServiceImpl.getAllClassesByUser(user);
    }
}
