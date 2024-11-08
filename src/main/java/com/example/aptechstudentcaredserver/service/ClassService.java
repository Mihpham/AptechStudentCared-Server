package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.AssignTeacherRequest;
import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
import com.example.aptechstudentcaredserver.bean.response.CourseWithClassesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClassService {
//    List<Class> getClassesByUser(Optional<User> user);
    public Page<ClassResponse> findAllClass(Pageable pageable);

    public CourseWithClassesResponse findClassWithSubjectByClassId(int classId);
    public ClassResponse findClassById(int classId);

    public void addClass(ClassRequest classRequest);

    public ClassResponse updateClass(int classId, ClassRequest classRequest);

    public void deleteClass(int classId);

    void assignTeacherToSubject(int classId, AssignTeacherRequest assignTeacherRequest);
}
