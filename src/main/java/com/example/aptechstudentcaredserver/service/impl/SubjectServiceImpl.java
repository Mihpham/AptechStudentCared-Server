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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;
    private final CourseSubjectRepository courseSubjectRepository;

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
        String generatedSubjectCode = generateSubjectCode(subjectRq.getSubjectName());
        if (subjectRepository.findBySubjectName(subjectRq.getSubjectName()).isPresent()) {
            throw new DuplicateException("Subject with the same name already exists.");
        }
        if (subjectRepository.findBySubjectCode(generatedSubjectCode).isPresent()) {
            throw new DuplicateException("Subject code already exists.");
        }
        Subject subject = new Subject();
        subject.setSubjectName(subjectRq.getSubjectName());
        subject.setSubjectCode(generatedSubjectCode);
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
            String newSubjectCode = generateSubjectCode(subjectRq.getSubjectName());
            if (subjectRepository.findBySubjectCode(newSubjectCode)
                    .filter(subject -> subject.getId() != subjectId)
                    .isPresent()) {
                throw new DuplicateException("Subject code already exists.");
            }
            existingSubject.setSubjectName(subjectRq.getSubjectName());
            existingSubject.setSubjectCode(newSubjectCode);
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
        List<CourseSubject> courseSubjects = courseSubjectRepository.findBySubjectId(subjectId);
        if (!courseSubjects.isEmpty()) {
            courseSubjectRepository.deleteAll(courseSubjects);
        }

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

    public String generateSubjectCode(String subjectName) {
        List<String> ignoreWords = Arrays.asList("in", "on", "at", "of", "the", "and", "a", "an", "to", "for");

        String[] words = subjectName.trim().split("\\s+");
        if (words.length == 1) {
            return words[0].toUpperCase().substring(0, Math.min(words[0].length(), 6));
        }
        StringBuilder code = new StringBuilder();
        for (String word : words) {
            if (code.length() < 6) {
                code.append(Character.toUpperCase(word.charAt(0)));
            } else {
                break;
            }
        }

        return code.toString();
    }

}
