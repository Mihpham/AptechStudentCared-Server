package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentPerformanceResponse {
    private String studentName;
    private String subjectCode;
    private BigDecimal theoreticalScore;
    private BigDecimal practicalScore;
    private BigDecimal attendancePercentage;
}
