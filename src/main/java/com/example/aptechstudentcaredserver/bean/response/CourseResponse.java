package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    private int id;
    private String courseName;
    private String courseCode;
    private String classSchedule;
    private int courseCompTime;
//    private Set<UserCourseResponse> userCourses;
//    private Set<CourseSubjectResponse> courseSubjects;
}
