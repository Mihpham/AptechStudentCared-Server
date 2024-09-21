package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.SubjectExamScoreRequest;
import com.example.aptechstudentcaredserver.bean.response.StudentExamScoreResponse;

import java.util.List;

public interface ExamDetailService {
    public List<StudentExamScoreResponse> getExamScoresByClass(int classId);

    public StudentExamScoreResponse updateStudentExamScore(SubjectExamScoreRequest scoreRequest, int classId);
}
