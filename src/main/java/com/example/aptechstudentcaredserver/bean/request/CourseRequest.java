package com.example.aptechstudentcaredserver.bean.request;

import lombok.Data;


@Data
public class CourseRequest {
    private String courseName;
    private String courseCode;
    private String classSchedule;
    private int courseCompTime;
}
