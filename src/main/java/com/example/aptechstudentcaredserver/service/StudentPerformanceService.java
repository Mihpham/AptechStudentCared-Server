package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.response.SubjectPerformance;

public interface StudentPerformanceService {
    public SubjectPerformance saveStudentPerformance(int userId, int subjectId, int classId);

}
