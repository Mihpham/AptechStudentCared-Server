package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    private int id;
    private String courseName;
    private String courseCode;
    private String classSchedule;
    private String courseCompTime;
    private List<SubjectResponse> subjects; // Thêm trường để chứa danh sách môn học
}
