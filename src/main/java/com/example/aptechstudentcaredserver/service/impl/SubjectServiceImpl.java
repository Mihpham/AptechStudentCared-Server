package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.SubjectRequest;
import com.example.aptechstudentcaredserver.bean.response.SubjectResponse;
import com.example.aptechstudentcaredserver.entity.CourseSubject;
import com.example.aptechstudentcaredserver.entity.Subject;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.exception.EmptyListException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.CourseSubjectRepository;
import com.example.aptechstudentcaredserver.repository.SubjectRepository;
import com.example.aptechstudentcaredserver.service.SubjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;
    private final CourseSubjectRepository courseSubjectRepository;

    @Override
    public List<SubjectResponse> findAllSubject(Pageable pageable) {
        try {
            Page<Subject> subjects = subjectRepository.findAll(pageable);
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
        if (subjectRepository.findBySubjectName(subjectRq.getSubjectName()).isPresent()) {
            throw new DuplicateException("Subject with the same name already exists.");
        }

        if (subjectRepository.findBySubjectCode(subjectRq.getSubjectCode()).isPresent()) {
            throw new DuplicateException("Subject code already exists.");
        }

        Subject subject = new Subject();
        subject.setSubjectName(subjectRq.getSubjectName());
        subject.setSubjectCode(subjectRq.getSubjectCode());
        subject.setTotalHours(subjectRq.getTotalHours());
        subject.setCreatedAt(LocalDateTime.now());
        subject.setUpdatedAt(LocalDateTime.now());
        subjectRepository.save(subject);
    }


    @Override
    public SubjectResponse updateSubject(int subjectId, SubjectRequest subjectRq) {
        try {
            Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);
            if (!optionalSubject.isPresent()) {
                throw new NotFoundException("Subject with ID " + subjectId + " not found.");
            }
            Subject existingSubject = optionalSubject.get();

            if (subjectRepository.findBySubjectName(subjectRq.getSubjectName())
                    .filter(subject -> subject.getId() != subjectId)
                    .isPresent()) {
                throw new DuplicateException("Subject with the same name already exists.");
            }

            if (subjectRepository.findBySubjectCode(subjectRq.getSubjectCode())
                    .filter(subject -> subject.getId() != subjectId)
                    .isPresent()) {
                throw new DuplicateException("Subject code already exists.");
            }

            existingSubject.setSubjectName(subjectRq.getSubjectName());
            existingSubject.setSubjectCode(subjectRq.getSubjectCode());
            existingSubject.setTotalHours(subjectRq.getTotalHours());
            existingSubject.setUpdatedAt(LocalDateTime.now());

            subjectRepository.save(existingSubject);

            return convertToSubjectResponse(existingSubject);

        } catch (DuplicateException e) {
            throw e;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update subject.", e);
        }
    }


    @Override
    public void deleteSubject(int subjectId) {
        // Tìm kiếm các liên kết liên quan và xóa chúng
        List<CourseSubject> courseSubjects = courseSubjectRepository.findBySubjectId(subjectId);
        if (!courseSubjects.isEmpty()) {
            courseSubjectRepository.deleteAll(courseSubjects);
        }

        // Xóa subject nếu tồn tại
        if (subjectRepository.existsById(subjectId)) {
            subjectRepository.deleteById(subjectId);
        } else {
            throw new NotFoundException("Subject with ID " + subjectId + " not found.");
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
