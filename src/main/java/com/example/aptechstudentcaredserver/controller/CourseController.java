package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.CourseRequest;
import com.example.aptechstudentcaredserver.bean.response.CourseResponse;
import com.example.aptechstudentcaredserver.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/courses")
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourseInfo(@PathVariable("courseId") int courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

    @PostMapping("/add")
    public ResponseEntity<String> createCourse(@RequestBody CourseRequest courseRequest) {
        try {
            courseService.createCourse(courseRequest);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body("{\"message\": \"Course added successfully\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable int courseId, @RequestBody CourseRequest courseRequest) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, courseRequest));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable int courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body("{\"message\": \"Course deleted successfully\"}");
    }
}
