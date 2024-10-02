package com.example.aptechstudentcaredserver.bean.response;

import jakarta.persistence.Column;
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
    private int presentCount;
    private int presentWithPermissionCount;
    private int absentCount;
    private BigDecimal practicalScore;
    private BigDecimal attendancePercentage;
    private BigDecimal practicalPercentage;
    private BigDecimal theoreticalPercentage;
}
