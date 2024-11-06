package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.CourseRequest;
import com.example.aptechstudentcaredserver.bean.response.CourseResponse;
import com.example.aptechstudentcaredserver.bean.response.ResponseMessage;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<List<CourseResponse>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        List<CourseResponse> courses = courseService.getAllCourses(pageable);
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
        try {
            courseService.updateCourse(courseId, courseRequest);
            return new ResponseEntity<>(new ResponseMessage("Course updated successfully"), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage()));
        } catch (DuplicateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<ResponseMessage> deleteCourse(@PathVariable int courseId) {
        try {
            courseService.deleteCourse(courseId);
            return new ResponseEntity<>(new ResponseMessage("Course deleted successfully"), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }
}
