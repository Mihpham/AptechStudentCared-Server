package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.SubjectRequest;
import com.example.aptechstudentcaredserver.bean.response.SubjectResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SubjectService {
    public List<SubjectResponse> findAllSubject(Pageable pageable);
    public SubjectResponse findSubjectById(int subjectId);
    public void createSubject(SubjectRequest subjectRq);
    public SubjectResponse updateSubject(int subjectId, SubjectRequest subjectRq);
    public void deleteSubject(int subjectId);
}
