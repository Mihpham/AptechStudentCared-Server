package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.AssignTeacherRequest;
import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassDetailResponse;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
import com.example.aptechstudentcaredserver.bean.response.CourseWithClassesResponse;
import com.example.aptechstudentcaredserver.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ClassService {
    //    List<Class> getClassesByUser(Optional<User> user);
    public Page<ClassResponse> findAllClass(Pageable pageable);

    public Map<String, Long> findClassCountBySemester();

    Page<ClassResponse> searchClass(String className, Pageable pageable);

    public CourseWithClassesResponse findClassWithSubjectByClassId(int classId);

    public ClassDetailResponse findClassById(int classId, Pageable pageable);

    Page<ClassResponse> findClassByStatus(Status status, Pageable pageable);

    public void addClass(ClassRequest classRequest);

    public ClassResponse updateClass(int classId, ClassRequest classRequest);

    public void deleteClass(int classId);

    void assignTeacherToSubject(int classId, AssignTeacherRequest assignTeacherRequest);
}
