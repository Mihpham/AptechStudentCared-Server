package com.example.aptechstudentcaredserver.bean.response;

import com.example.aptechstudentcaredserver.bean.request.SubjectExamScoreRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class StudentExamScoreResponse {
    private String studentName;
    private List<SubjectExamScoreRequest> subjects;
}
