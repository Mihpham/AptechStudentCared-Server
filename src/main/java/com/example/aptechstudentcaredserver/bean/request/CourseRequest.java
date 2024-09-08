package com.example.aptechstudentcaredserver.bean.request;

import lombok.Data;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CourseRequest {
    private String courseName;
    private String courseCode;
    private String completionTime;
    private String classSchedule;
    private String courseCompTime;

    private Map<String, List<String>> semesters = new HashMap<>();
}
