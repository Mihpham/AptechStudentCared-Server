package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.CourseRequest;
import com.example.aptechstudentcaredserver.entity.Course;
import com.example.aptechstudentcaredserver.repository.CourseRepository;
import com.example.aptechstudentcaredserver.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;

    @Override
    public void addCourse(CourseRequest courseRequest) {
        Course existingCourse = courseRepository.findByCourseName(courseRequest.getCourseName());
        if (existingCourse != null) {
            throw new RuntimeException("Course with this name already exists");
        }

        // Create new Course
        Course newCourse = new Course();
        newCourse.setCourseName(courseRequest.getCourseName());
        newCourse.setCourseCode(courseRequest.getCourseCode());
        newCourse.setClassSchedule(courseRequest.getClassSchedule());
        newCourse.setCreatedAt(LocalDateTime.now());
        newCourse.setUpdatedAt(LocalDateTime.now());

        // Save new Course
        courseRepository.save(newCourse);
    }
}
