package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class StudentExamScoreResponse {
    private String className;
    private String rollNumber;
    private String studentName;
    private String subjectCode;
    private BigDecimal theoreticalScore;
    private BigDecimal practicalScore;
}
