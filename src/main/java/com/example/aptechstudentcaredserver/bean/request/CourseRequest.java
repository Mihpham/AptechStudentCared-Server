package com.example.aptechstudentcaredserver.bean.request;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


@Data
public class CourseRequest {
    private String courseName;
    private String courseCode;
    private String classSchedule;
    private Timestamp courseCompTime;
    private Map<Integer, List<Integer>> subjectsPerSemester;
}
