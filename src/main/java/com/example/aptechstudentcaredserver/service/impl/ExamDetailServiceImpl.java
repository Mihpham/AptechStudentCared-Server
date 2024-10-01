package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.StudentExamScoreRequest;
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
        List<StudentExamScoreResponse> examScoresResponse = new ArrayList<>();

        // Fetch class name based on classId (assuming the class entity holds the name)
        String className = classMembers.stream()
                .findFirst()
                .map(member -> member.getClasses().getClassName())
                .orElse("Unknown Class");

        // Loop through each class member (students)
        for (GroupClass member : classMembers) {
            List<StudentExamScoreRequest> scoresList = new ArrayList<>();

            // Fetch all subjects for the class
            List<Subject> subjects = subjectRepository.findAll();

            for (Subject subject : subjects) {
                // Fetch scores for both theoretical and practical exams
                BigDecimal theoreticalScore = examDetailRepository
                        .findByUserIdAndSubjectIdAndExamType(member.getUser().getId(),
                                subject.getId(), MarkType.THEORETICAL)
                        .map(ExamDetail::getScore)
                        .orElse(BigDecimal.ZERO);

                BigDecimal practicalScore = examDetailRepository
                        .findByUserIdAndSubjectIdAndExamType(member.getUser().getId(),
                                subject.getId(), MarkType.PRACTICAL)
                        .map(ExamDetail::getScore)
                        .orElse(BigDecimal.ZERO);

                // Create a new score request for each subject
                StudentExamScoreRequest scoreRequest = StudentExamScoreRequest.builder()
                        .classId(classId) // Set the classId
                        .className(className) // Set the className
                        .rollNumber(member.getUser().getUserDetail().getRollNumber())
                        .studentName(member.getUser().getUserDetail().getFullName())
                        .subjectCode(subject.getSubjectCode())
                        .theoreticalScore(theoreticalScore)
                        .practicalScore(practicalScore)
                        .build();

                scoresList.add(scoreRequest);
            }

            // Create response for each student
            StudentExamScoreResponse response = StudentExamScoreResponse.builder()
                    .className(className)
                    .listExamScore(scoresList)
                    .build();

            examScoresResponse.add(response);
        }

        return examScoresResponse;
    }

    @Override
    public StudentExamScoreResponse updateStudentExamScore(StudentExamScoreRequest scoreRequest, int classId) {
        // Find the class members
        List<GroupClass> classMembers = groupClassRepository.findByClassesId(classId);

        // Find the student by rollNumber and fullName
        User student = classMembers.stream()
                .map(GroupClass::getUser)
                .filter(u -> u.getUserDetail().getRollNumber().equals(scoreRequest.getRollNumber()) &&
                        u.getUserDetail().getFullName().equals(scoreRequest.getStudentName()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Student with rollNumber '" + scoreRequest.getRollNumber()
                        + "' and name '" + scoreRequest.getStudentName() + "' not found in the specified class"));

        // Find the subject by code
        Subject subject = subjectRepository.findBySubjectCode(scoreRequest.getSubjectCode())
                .orElseThrow(() -> new NotFoundException("Subject '" + scoreRequest.getSubjectCode()
                        + "' not found"));

        // Update or create the theoretical score
        ExamDetail theoreticalDetail = examDetailRepository.findByUserIdAndSubjectIdAndExamType(
                        student.getId(), subject.getId(), MarkType.THEORETICAL)
                .orElse(new ExamDetail());
        theoreticalDetail.setUser(student);
        theoreticalDetail.setSubject(subject);
        theoreticalDetail.setExamType(MarkType.THEORETICAL);

        // Chỉ cập nhật điểm lý thuyết nếu không null
        if (scoreRequest.getTheoreticalScore() != null) {
            theoreticalDetail.setScore(scoreRequest.getTheoreticalScore());
        }
        theoreticalDetail.setUpdatedAt(LocalDateTime.now());
        examDetailRepository.save(theoreticalDetail);

        // Cập nhật hoặc tạo điểm thực hành
        ExamDetail practicalDetail = examDetailRepository.findByUserIdAndSubjectIdAndExamType(
                        student.getId(), subject.getId(), MarkType.PRACTICAL)
                .orElse(new ExamDetail());
        practicalDetail.setUser(student);
        practicalDetail.setSubject(subject);
        practicalDetail.setExamType(MarkType.PRACTICAL);

        // Chỉ cập nhật điểm thực hành nếu không null
        if (scoreRequest.getPracticalScore() != null) {
            practicalDetail.setScore(scoreRequest.getPracticalScore());
        }
        practicalDetail.setUpdatedAt(LocalDateTime.now());
        examDetailRepository.save(practicalDetail);

        // Tạo và trả về phản hồi cho môn học đã cập nhật
        return convertToResponse(student, subject);
    }


    private StudentExamScoreResponse convertToResponse(User user, Subject subject) {
        // Fetch scores for theoretical and practical exams
        BigDecimal theoreticalScore = examDetailRepository.findByUserIdAndSubjectIdAndExamType(
                        user.getId(), subject.getId(), MarkType.THEORETICAL)
                .map(ExamDetail::getScore)
                .orElse(BigDecimal.ZERO);

        BigDecimal practicalScore = examDetailRepository.findByUserIdAndSubjectIdAndExamType(
                        user.getId(), subject.getId(), MarkType.PRACTICAL)
                .map(ExamDetail::getScore)
                .orElse(BigDecimal.ZERO);

        // Create a new score request for the response
        StudentExamScoreRequest scoreRequest = StudentExamScoreRequest.builder()
                .classId(user.getGroupClasses().stream()
                        .findFirst() // Assuming user is linked to one class
                        .map(groupClass -> groupClass.getClasses().getId())
                        .orElseThrow(() -> new NotFoundException("Class not found with")))
                .className(user.getGroupClasses().stream()
                        .findFirst() // Assuming user is linked to one class
                        .map(groupClass -> groupClass.getClasses().getClassName())
                        .orElse("Unknown Class")) // Set the className
                .rollNumber(user.getUserDetail().getRollNumber())
                .studentName(user.getUserDetail().getFullName())
                .subjectCode(subject.getSubjectCode())
                .theoreticalScore(theoreticalScore)
                .practicalScore(practicalScore)
                .build();

        // Create and return the response containing class information and scores
        return StudentExamScoreResponse.builder()
                .className(user.getGroupClasses().stream()
                        .findFirst() // Assuming user is linked to one class
                        .map(groupClass -> groupClass.getClasses().getClassName())
                        .orElse("Unknown Class"))
                .listExamScore(List.of(scoreRequest)) // Wrap in a list
                .build();
    }
}
