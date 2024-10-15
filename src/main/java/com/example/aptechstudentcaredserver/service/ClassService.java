package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.AssignTeacherRequest;
import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
import com.example.aptechstudentcaredserver.bean.response.CourseWithClassesResponse;
import com.example.aptechstudentcaredserver.bean.response.StudentPerformanceResponse;
import com.example.aptechstudentcaredserver.bean.response.SubjectInfoResponse;
import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ClassService {
//    List<Class> getClassesByUser(Optional<User> user);
    public List<ClassResponse> findAllClass();

    public CourseWithClassesResponse findClassWithSubjectByClassId(int classId);

    public Map<String, List<StudentPerformanceResponse>> getAllSubjectsBySemester(int classId, String semesterName, int userId);

    public ClassResponse findClassById(int classId);

    public void addClass(ClassRequest classRequest);

    public ClassResponse updateClass(int classId, ClassRequest classRequest);

    public void deleteClass(int classId);

    void assignTeacherToSubject(int classId, AssignTeacherRequest assignTeacherRequest);
}
