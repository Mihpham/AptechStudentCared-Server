package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.SubjectRequest;
import com.example.aptechstudentcaredserver.bean.response.SubjectResponse;
import com.example.aptechstudentcaredserver.entity.Subject;
import com.example.aptechstudentcaredserver.exception.EmptyListException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.SubjectRepository;
import com.example.aptechstudentcaredserver.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;

    @Override
    public List<SubjectResponse> findAllSubject() {
        try {
            List<Subject> subjects = subjectRepository.findAll();
            if (subjects.isEmpty()) {
                throw new EmptyListException("No subjects found.");
            }
            return subjects.stream()
                    .map(this::convertToSubjectResponse)
                    .collect(Collectors.toList());
        } catch (EmptyListException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve subjects.", e);
        }
    }

    @Override
    public SubjectResponse findSubjectById(int subjectId) {
        Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);
        Subject subject = optionalSubject.orElseThrow(() ->
                new NotFoundException("Subject with ID " + subjectId + " not found")
        );
        return convertToSubjectResponse(subject);
    }



    @Override
    public void createSubject(SubjectRequest subjectRq) {
        try {
            if (subjectRq.getSubjectName() == null || subjectRq.getSubjectName().isEmpty()) {
                throw new IllegalArgumentException("Subject name cannot be null or empty.");
            }

            Subject subject = new Subject();
            subject.setSubjectName(subjectRq.getSubjectName());
            subject.setSubjectCode(subjectRq.getSubjectCode());
            subject.setTotalHours(subjectRq.getTotalHours());
            subject.setCreatedAt(LocalDateTime.now());
            subject.setUpdatedAt(LocalDateTime.now());
            subjectRepository.save(subject);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Subject with the same name already exists.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create subject.", e);
        }
    }


    @Override
    public SubjectResponse updateSubject(int subjectId, SubjectRequest subjectRq) {
        try {
            Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);
            if (optionalSubject.isPresent()) {
                Subject subject = optionalSubject.get();
                subject.setSubjectName(subjectRq.getSubjectName());
                subject.setSubjectCode(subjectRq.getSubjectCode());
                subject.setTotalHours(subjectRq.getTotalHours());
                subject.setUpdatedAt(LocalDateTime.now());
                subjectRepository.save(subject);
                return convertToSubjectResponse(subject);
            } else {
                throw new NotFoundException("Subject with ID " + subjectId + " not found.");
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update subject.", e);
        }
    }

    @Override
    public void deleteSubject(int subjectId) {
        try {
            if (subjectRepository.existsById(subjectId)) {
                subjectRepository.deleteById(subjectId);
            } else {
                throw new NotFoundException("Subject with ID " + subjectId + " not found.");
            }
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Subject with ID " + subjectId + " does not exist." + e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete subject.", e);
        }
    }


    public SubjectResponse convertToSubjectResponse(Subject subject) {
        if (subject == null) {
            throw new IllegalArgumentException("Subject cannot be null.");
        }
        SubjectResponse response = new SubjectResponse();
        response.setId(subject.getId());
        response.setSubjectName(subject.getSubjectName());
        response.setSubjectCode(subject.getSubjectCode());
        response.setTotalHours(subject.getTotalHours());
        response.setCreatedAt(subject.getCreatedAt());
        response.setUpdatedAt(subject.getUpdatedAt());

        return response;
    }




}
