package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.AssignTeacherRequest;
import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
import com.example.aptechstudentcaredserver.bean.response.CourseWithClassesResponse;
import com.example.aptechstudentcaredserver.bean.response.ResponseMessage;
import com.example.aptechstudentcaredserver.entity.User;
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

        // Create a Pageable object with the page number and page size
        Pageable pageable = PageRequest.of(page, size);

        // Fetch the paginated class responses from the service
        Page<ClassResponse> classResponses = classService.findAllClass(pageable);

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
