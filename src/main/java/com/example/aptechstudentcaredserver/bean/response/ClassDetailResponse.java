package com.example.aptechstudentcaredserver.bean.response;

import com.example.aptechstudentcaredserver.enums.DayOfWeeks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassDetailResponse {
    private int id;
    private String className;
    private String center;
    private LocalTime startHour;
    private LocalTime endHour;
    private List<DayOfWeeks> days;
    private String status;
    private String semesterName;
    private CourseResponse course;
    private List<StudentResponse> students;
    private List<SubjectTeacherResponse> subjectTeachers;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;
}
