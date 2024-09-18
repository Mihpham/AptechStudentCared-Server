package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;

import java.util.List;

public interface ClassService {

    public List<ClassResponse> findAllClass();

    public ClassResponse findClassById(int classId);
    public void addClass(ClassRequest classRequest);

    public ClassResponse updateClass(int classId,ClassRequest classRequest);
    public  void  deleteClass(int classId);

    void assignTeacherToSubject(String subjectCode, String teacherName);
}
