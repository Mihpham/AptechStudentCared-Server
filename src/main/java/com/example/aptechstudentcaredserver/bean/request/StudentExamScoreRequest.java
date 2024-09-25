package com.example.aptechstudentcaredserver.bean.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class StudentExamScoreRequest {
    private int classId;
    private String className;
    private String rollNumber;
    private String studentName;
    private String subjectCode;
    private BigDecimal theoreticalScore;
    private BigDecimal practicalScore;
}
