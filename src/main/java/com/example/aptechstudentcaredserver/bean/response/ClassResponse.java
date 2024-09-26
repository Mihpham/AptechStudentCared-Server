package com.example.aptechstudentcaredserver.bean.response;

import com.example.aptechstudentcaredserver.enums.DayOfWeeks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponse {
    private int id;
    private String className;
    private String center;
    private LocalTime startHour;
    private LocalTime endHour;
    private List<DayOfWeeks> days;
    private LocalDateTime createdAt;
    private String status;
    private String sem;
    private CourseResponse course;
    private List<StudentResponse> students;
    private List<SubjectTeacherResponse> subjectTeachers;
}
