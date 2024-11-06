package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.response.StudentPerformanceResponse;
import com.example.aptechstudentcaredserver.bean.response.SubjectPerformance;

public interface StudentPerformanceService {
    public SubjectPerformance saveStudentPerformance(int userId, int subjectId, int classId);
    StudentPerformanceResponse getAllSubjectsBySemester(int classId, String semesterName, int userId);

}
