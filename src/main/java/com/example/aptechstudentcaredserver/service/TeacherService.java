package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.TeacherRequest;
import com.example.aptechstudentcaredserver.bean.response.TeacherResponse;

import java.util.List;

public interface TeacherService {
    void registerTeacher(TeacherRequest teacherRequest);

     List<TeacherResponse> findAllTeachers();

     TeacherResponse findTeacherById(int teacherId);

     TeacherResponse updateTeacher(TeacherRequest teacherRequest,int teacherId);

     void deleteTeacher(int teacherId);
    List<TeacherResponse> findTeachersByClassId(int classId);

}
