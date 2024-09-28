package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.AssignTeacherRequest;
import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
import com.example.aptechstudentcaredserver.bean.response.CourseWithClassesResponse;
import com.example.aptechstudentcaredserver.bean.response.ResponseMessage;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.service.ClassService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/classes")
public class ClassController {
    private final ClassService classService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ClassResponse>> findAllClass() {
        try {
            List<ClassResponse> classResponses = classService.findAllClass();
            return new ResponseEntity<>(classResponses, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of()); // Return empty list or handle it accordingly
        }
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<CourseWithClassesResponse> findClassWithSubjectByClassId(@PathVariable int classId) {
        CourseWithClassesResponse classDetails = classService.findClassWithSubjectByClassId(classId);
        return new ResponseEntity<>(classDetails, HttpStatus.OK);
    }

    @GetMapping("/{classId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ClassResponse> findClassById(@PathVariable int classId) {
        try {
            ClassResponse classResponse = classService.findClassById(classId);
            if (classResponse == null) {
                throw new NotFoundException("Class not found with ID: " + classId);
            }
            return new ResponseEntity<>(classResponse, HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage> addClass(@Valid @RequestBody ClassRequest classRequest) {
        try {
            classService.addClass(classRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("Class added successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("An unexpected error occurred.")); // Handle other exceptions
        }
    }

    @PutMapping("/{classId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> updateClass(@Valid @RequestBody ClassRequest classRequest, @PathVariable int classId) {
        try {
            ClassResponse updatedClass = classService.updateClass(classId, classRequest);

            if (updatedClass == null) {
                throw new NotFoundException("Class not found with ID: " + classId);
            }
            return new ResponseEntity<>(updatedClass, HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("An unexpected error occurred."));
        }
    }

    @DeleteMapping("/{classId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage> deleteClass(@PathVariable int classId) {
        try {
            classService.deleteClass(classId);
            return new ResponseEntity<>(new ResponseMessage("Class deleted successfully"), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage())); // Return 404
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage())); // Return 400
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("An unexpected error occurred.")); // Return 500
        }
    }


    @PutMapping("/{classId}/assign-teacher")
    public ResponseEntity<String> assignTeacherToSubject(
            @PathVariable int classId,
            @RequestBody AssignTeacherRequest request) {
        try {
            classService.assignTeacherToSubject(classId, request);
            return ResponseEntity.ok("Assign Teacher successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred.");
        }
    }
}