package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.CourseRequest;
import com.example.aptechstudentcaredserver.bean.response.CourseResponse;

import java.util.List;

public interface CourseService {

    List<CourseResponse> getAllCourses();

    CourseResponse getCourseById(int CourseId);

    void createCourse(CourseRequest courseRequest);

    CourseResponse updateCourse(int courseId, CourseRequest courseRequest);

    void deleteCourse(int courseId);

}
