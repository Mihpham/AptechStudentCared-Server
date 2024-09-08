package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.CourseRequest;
import com.example.aptechstudentcaredserver.bean.response.CourseResponse;
import com.example.aptechstudentcaredserver.bean.response.ResponseMessage;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/courses")
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> courses = courseService.getAllCourses();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable int courseId) {
        CourseResponse courseResponse = courseService.getCourseById(courseId);
        return new ResponseEntity<>(courseResponse, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseMessage> createCourse(@RequestBody CourseRequest courseRequest) {
        try {
            courseService.createCourse(courseRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("Course added successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<ResponseMessage> updateCourse(@RequestBody CourseRequest courseRequest, @PathVariable int courseId) {
            courseService.updateCourse(courseId, courseRequest);
            return new ResponseEntity<>(new ResponseMessage("Course updated successfully"), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<ResponseMessage> deleteCourse(@PathVariable int courseId) {
        courseService.deleteCourse(courseId);
        return new ResponseEntity<>(new ResponseMessage("Course deleted successfully"), HttpStatus.ACCEPTED);
    }
}
