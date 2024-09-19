package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;

import java.util.List;
import java.util.Map;

public interface ClassService {

    public List<ClassResponse> findAllClass();

    Map<String, List<String>> getAllSubjectsBySemester(int classId, String semesterName);

    public ClassResponse findClassById(int classId);

    public void addClass(ClassRequest classRequest);

    public ClassResponse updateClass(int classId, ClassRequest classRequest);

    public void deleteClass(int classId);

    void assignTeacherToSubject(int classId,String subjectCode, String teacherName);
}
