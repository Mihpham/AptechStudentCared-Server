package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
import com.example.aptechstudentcaredserver.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @PostMapping("/add")
    public ResponseEntity<String> addClass(@RequestBody ClassRequest classRequest) {
        try {
            classService.addClass(classRequest);
            return ResponseEntity.ok("Class added successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{classId}")
    public ResponseEntity<ClassResponse> updateClass(@RequestBody ClassRequest classRequest, @PathVariable int classId) {
        ClassResponse updateClass = classService.updateClass(classId, classRequest);
        return new ResponseEntity<>(updateClass, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{classId}")
    public ResponseEntity<String> deleteClass(@PathVariable int classId) {
        classService.deleteClass(classId);
        return new ResponseEntity<>("Delete successfully", HttpStatus.OK);
    }
}
