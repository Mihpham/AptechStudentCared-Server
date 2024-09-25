package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.AssignTeacherRequest;
import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
import com.example.aptechstudentcaredserver.bean.response.CourseResponse;
import com.example.aptechstudentcaredserver.bean.response.CourseWithClassesResponse;

import java.util.List;
import java.util.Map;

public interface ClassService {

    public List<ClassResponse> findAllClass();

    public CourseWithClassesResponse findClassWithSubjectByClassId(int classId);

    Map<String, List<String>> getAllSubjectsBySemester(int classId, String semesterName);

    public ClassResponse findClassById(int classId);

    public void addClass(ClassRequest classRequest);

    public ClassResponse updateClass(int classId, ClassRequest classRequest);

    public void deleteClass(int classId);

    void assignTeacherToSubject(int classId, AssignTeacherRequest assignTeacherRequest);
}
