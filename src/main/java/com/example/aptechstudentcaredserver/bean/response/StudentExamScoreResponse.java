package com.example.aptechstudentcaredserver.bean.response;

import com.example.aptechstudentcaredserver.bean.request.StudentExamScoreRequest;
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
    private List<StudentExamScoreRequest> listExamScore;
}
