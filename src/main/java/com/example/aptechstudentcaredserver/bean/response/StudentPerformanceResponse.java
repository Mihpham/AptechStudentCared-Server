package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentPerformanceResponse {
    private String firstSubjectSchedules;
    private String lastSubjectSchedules;
    private List<SubjectPerformance> subjectPerformances;
}
