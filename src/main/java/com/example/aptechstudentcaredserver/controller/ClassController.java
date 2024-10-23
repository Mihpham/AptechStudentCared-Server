package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.AssignTeacherRequest;
import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.*;
import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.repository.UserRepository;
import com.example.aptechstudentcaredserver.service.ClassService;
import com.example.aptechstudentcaredserver.service.impl.ClassServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/classes")
public class ClassController {
    private final ClassService classService;
    private final ClassServiceImpl classServiceImpl;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<List<ClassResponse>> findAllClass() {
        List<ClassResponse> classResponses = classService.findAllClass();
        return new ResponseEntity<>(classResponses, HttpStatus.OK);
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<CourseWithClassesResponse> findClassWithSubjectByClassId(@PathVariable int classId) {
        CourseWithClassesResponse classDetails = classService.findClassWithSubjectByClassId(classId);
        return new ResponseEntity<>(classDetails, HttpStatus.OK);
    }

    @GetMapping("/{classId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<ClassResponse> findClassById(@PathVariable int classId) {
        ClassResponse classResponse = classService.findClassById(classId);
        return new ResponseEntity<>(classResponse, HttpStatus.OK);
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

    @GetMapping("/{classId}/user/{userId}/subjects")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN') or hasRole('ROLE_SRO') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> getAllSubjectsBySemester(
            @PathVariable int classId,
            @PathVariable int userId,
            @RequestParam(required = false) String semesterName) {
        try {
            Map<String, List<StudentPerformanceResponse>> semesterSubjects = classService.getAllSubjectsBySemester(classId, semesterName, userId);
            return new ResponseEntity<>(semesterSubjects, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        }
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
