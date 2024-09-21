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

public class SubjectExamScoreRequest {
    private String studentName;
    private String subjectName;
    private BigDecimal theoreticalScore;
    private BigDecimal practicalScore;
    private int classId;
}
