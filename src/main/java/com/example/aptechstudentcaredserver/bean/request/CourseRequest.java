package com.example.aptechstudentcaredserver.bean.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequest {
    private int id;
    private String courseName;
    private String courseCode;
    private String courseCompTime;
    private Map<String, List<String>> semesters = new HashMap<>();
}
