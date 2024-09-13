package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.TeacherRequest;
import com.example.aptechstudentcaredserver.bean.response.TeacherResponse;

import java.util.List;

public interface TeacherService {
    public void registerTeacher(TeacherRequest teacherRequest);

    public List<TeacherResponse> findAllTeachers();

    public TeacherResponse findTeacherById(int teacherId);

    public TeacherResponse updateTeacher(TeacherRequest teacherRequest,int teacherId);

    public void deleteTeacher(int teacherId);

}
