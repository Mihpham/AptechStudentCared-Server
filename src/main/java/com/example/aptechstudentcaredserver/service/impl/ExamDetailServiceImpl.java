package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.SubjectExamScoreRequest;
import com.example.aptechstudentcaredserver.bean.response.StudentExamScoreResponse;
import com.example.aptechstudentcaredserver.entity.ExamDetail;
import com.example.aptechstudentcaredserver.entity.GroupClass;
import com.example.aptechstudentcaredserver.entity.Subject;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.enums.MarkType;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.ExamDetailRepository;
import com.example.aptechstudentcaredserver.repository.GroupClassRepository;
import com.example.aptechstudentcaredserver.repository.SubjectRepository;
import com.example.aptechstudentcaredserver.repository.UserRepository;
import com.example.aptechstudentcaredserver.service.ExamDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExamDetailServiceImpl implements ExamDetailService {
    private final GroupClassRepository groupClassRepository;
    private final ExamDetailRepository examDetailRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    @Override
    public List<StudentExamScoreResponse> getExamScoresByClass(int classId) {
        List<GroupClass> classMembers = groupClassRepository.findByClassesId(classId);
        List<StudentExamScoreResponse> examScores = new ArrayList<>();

        for (GroupClass member : classMembers) {
            List<Subject> subjects = subjectRepository.findAll();
            List<SubjectExamScoreRequest> subjectScores = new ArrayList<>();

            for (Subject subject : subjects) {
                // Check for theoretical score
                Optional<ExamDetail> theoreticalDetailOpt = examDetailRepository.findByUserIdAndSubjectIdAndExamType(
                        member.getUser().getId(), subject.getId(), MarkType.THEORETICAL);
                BigDecimal theoreticalScore = theoreticalDetailOpt.map(ExamDetail::getScore).orElse(BigDecimal.ZERO);

                // Check for practical score
                Optional<ExamDetail> practicalDetailOpt = examDetailRepository.findByUserIdAndSubjectIdAndExamType(
                        member.getUser().getId(), subject.getId(), MarkType.PRACTICAL);
                BigDecimal practicalScore = practicalDetailOpt.map(ExamDetail::getScore).orElse(BigDecimal.ZERO);

                // Add the subject exam score to the list (removing studentId)
                subjectScores.add(SubjectExamScoreRequest.builder()
                        .rollNumber(member.getUser().getUserDetail().getRollNumber()) // Set studentName here
                        .subjectCode(subject.getSubjectCode())
                        .theoreticalScore(theoreticalScore)
                        .practicalScore(practicalScore)
                        .classId(classId)
                        .build());
            }

            // Build response object for each student
            StudentExamScoreResponse response = StudentExamScoreResponse.builder()
                    .rollNumber(member.getUser().getUserDetail().getRollNumber())
                    .subjects(subjectScores)
                    .build();

            examScores.add(response);
        }
        return examScores;
    }

    @Override
    public StudentExamScoreResponse updateStudentExamScore(SubjectExamScoreRequest scoreRequest, int classId) {
        // Find the class members
        List<GroupClass> classMembers = groupClassRepository.findByClassesId(classId);

        // Find the student by name in the class
        User student = classMembers.stream()
                .map(GroupClass::getUser)
                .filter(u -> u.getUserDetail().getRollNumber().equals(scoreRequest.getRollNumber()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Student '" + scoreRequest.getRollNumber() + "' not found in the specified class"));

        // Find the subject
        Subject subject = subjectRepository.findBySubjectCode(scoreRequest.getSubjectCode())
                .orElseThrow(() -> new RuntimeException("Subject '" + scoreRequest.getSubjectCode() + "' not found"));

        // Update or create the theoretical score
        ExamDetail theoreticalDetail = examDetailRepository.findByUserIdAndSubjectIdAndExamType(
                        student.getId(), subject.getId(), MarkType.THEORETICAL)
                .orElse(new ExamDetail());
        theoreticalDetail.setUser(student);
        theoreticalDetail.setSubject(subject);
        theoreticalDetail.setExamType(MarkType.THEORETICAL);
        theoreticalDetail.setScore(scoreRequest.getTheoreticalScore());
        theoreticalDetail.setUpdatedAt(LocalDateTime.now());
        examDetailRepository.save(theoreticalDetail);

        // Update or create the practical score
        ExamDetail practicalDetail = examDetailRepository.findByUserIdAndSubjectIdAndExamType(
                        student.getId(), subject.getId(), MarkType.PRACTICAL)
                .orElse(new ExamDetail());
        practicalDetail.setUser(student);
        practicalDetail.setSubject(subject);
        practicalDetail.setExamType(MarkType.PRACTICAL);
        practicalDetail.setScore(scoreRequest.getPracticalScore());
        practicalDetail.setUpdatedAt(LocalDateTime.now());
        examDetailRepository.save(practicalDetail);

        // Convert to response object
        return convertToResponse(student, subject);
    }

    private StudentExamScoreResponse convertToResponse(User user, Subject subject) {
        // Fetch scores for response
        BigDecimal theoreticalScore = examDetailRepository.findByUserIdAndSubjectIdAndExamType(
                        user.getId(), subject.getId(), MarkType.THEORETICAL)
                .map(ExamDetail::getScore).orElse(BigDecimal.ZERO);

        BigDecimal practicalScore = examDetailRepository.findByUserIdAndSubjectIdAndExamType(
                        user.getId(), subject.getId(), MarkType.PRACTICAL)
                .map(ExamDetail::getScore).orElse(BigDecimal.ZERO);

        // Create response
        SubjectExamScoreRequest subjectScore = SubjectExamScoreRequest.builder()
                .rollNumber(user.getUserDetail().getRollNumber())
                .subjectCode(subject.getSubjectCode())
                .theoreticalScore(theoreticalScore)
                .practicalScore(practicalScore)
                .build();

        return StudentExamScoreResponse.builder()
                .rollNumber(user.getUserDetail().getRollNumber())
                .subjects(List.of(subjectScore))
                .build();
    }
}
