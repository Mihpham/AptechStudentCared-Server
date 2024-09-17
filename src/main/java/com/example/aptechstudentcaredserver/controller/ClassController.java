package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
import com.example.aptechstudentcaredserver.bean.response.ResponseMessage;
import com.example.aptechstudentcaredserver.service.ClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/classes")
public class ClassController {
    private final ClassService classService;


    @GetMapping
    public ResponseEntity<List<ClassResponse>> findAllClass() {
        List<ClassResponse> classResponses = classService.findAllClass();
        return new ResponseEntity<>(classResponses, HttpStatus.OK);
    }

    @GetMapping("/{classId}")
    public ResponseEntity<ClassResponse> findClassById(@PathVariable int classId) {
        ClassResponse classResponse = classService.findClassById(classId);
        return new ResponseEntity<>(classResponse, HttpStatus.OK);
    }

    @GetMapping("/{classId}/subjects")
    public ResponseEntity<Map<String, List<String>>> getAllSubjectsBySemester(
            @PathVariable int classId,
            @RequestParam(required = false) String semesterName) {
        Map<String, List<String>> subjects = classService.getAllSubjectsBySemester(classId, semesterName);
        return ResponseEntity.ok(subjects);
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseMessage> addClass(@Valid @RequestBody ClassRequest classRequest) {
        try {
            classService.addClass(classRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("Class added successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @PutMapping("/{classId}")
    public ResponseEntity<ClassResponse> updateClass(@RequestBody ClassRequest classRequest, @PathVariable int classId) {
        ClassResponse updateClass = classService.updateClass(classId, classRequest);
        return new ResponseEntity<>(updateClass, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{classId}")
    public ResponseEntity<ResponseMessage> deleteClass(@PathVariable int classId) {
        classService.deleteClass(classId);
        return new ResponseEntity<>(new ResponseMessage("Class deleted successfully"), HttpStatus.ACCEPTED);
    }
}
