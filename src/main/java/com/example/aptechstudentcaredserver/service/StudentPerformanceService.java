package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.response.StudentPerformanceResponse;

public interface StudentPerformanceService {
    public StudentPerformanceResponse saveStudentPerformance(int userId, int subjectId, int classId);

}
