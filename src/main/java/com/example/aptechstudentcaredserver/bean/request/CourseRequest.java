package com.example.aptechstudentcaredserver.bean.request;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CourseRequest {
    private String courseName;
    private String courseCode;
    private String courseCompTime;
    private Map<String, List<String>> semesters = new HashMap<>();
}
