package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    private int id;
    private String courseName;
    private String courseCode;
    private String classSchedule;
    private Timestamp courseCompTime;
//    private Set<UserCourseResponse> userCourses;
//    private Set<CourseSubjectResponse> courseSubjects;
}
