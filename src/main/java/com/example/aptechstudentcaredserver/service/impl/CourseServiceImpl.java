package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.CourseRequest;
import com.example.aptechstudentcaredserver.bean.response.CourseResponse;
import com.example.aptechstudentcaredserver.entity.Course;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.CourseRepository;
import com.example.aptechstudentcaredserver.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;


    @Override
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::convertToCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CourseResponse getCourseById(int id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found"));
        return convertToCourseResponse(course);
    }

    @Override
    public void createCourse(CourseRequest courseRequest) {
        Course course = new Course();
        course.setCourseName(courseRequest.getCourseName());
        course.setCourseCode(courseRequest.getCourseCode());
        course.setClassSchedule(courseRequest.getClassSchedule());
        course.setCourseCompTime(courseRequest.getCourseCompTime()); // Set course completion time
//        course.setCreatedAt(LocalDateTime.now());
//        course.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    public CourseResponse updateCourse(int id, CourseRequest courseRequest) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found"));
        course.setCourseName(courseRequest.getCourseName());
        course.setCourseCode(courseRequest.getCourseCode());
        course.setClassSchedule(courseRequest.getClassSchedule());
        course.setCourseCompTime(courseRequest.getCourseCompTime()); // Update course completion time

        Course updatedCourse = courseRepository.save(course);
        return convertToCourseResponse(updatedCourse);
    }

    @Override
    public void deleteCourse(int id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        courseRepository.delete(course);
    }

    private CourseResponse convertToCourseResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getCourseName(),
                course.getCourseCode(),
                course.getClassSchedule(),
                course.getCourseCompTime()
        );
    }
}
