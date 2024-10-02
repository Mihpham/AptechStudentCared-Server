package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.response.StudentPerformanceResponse;
import com.example.aptechstudentcaredserver.entity.*;
import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.enums.MarkType;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.*;
import com.example.aptechstudentcaredserver.service.StudentPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentPerformanceServiceImpl implements StudentPerformanceService {

    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final ExamDetailRepository examDetailRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentPerformanceRepository studentPerformanceRepository;
    private final SubjectRepository subjectRepository;

    @Override
    public StudentPerformanceResponse saveStudentPerformance(int userId, int subjectId, int classId) {
        User student = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Student not found with id " + userId));

        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found with id " + classId));

        List<Attendance> attendances = attendanceRepository.findByUserId(student.getId());
        long totalClasses = attendanceRepository.countByUserIdAndSchedule_Classes_Id(userId, classId);

        int presentCount = 0;
        int presentWithPermissionCount = 0;
        int absentCount = 0;
        BigDecimal attendancePercentage = BigDecimal.ZERO;

        if (totalClasses > 0) {
            // Count attendance types
            presentCount = (int) attendances.stream()
                    .filter(a -> "P".equals(a.getAttendance1())).count();
            presentWithPermissionCount = (int) attendances.stream()
                    .filter(a -> "PA".equals(a.getAttendance1())).count();
            absentCount = (int) attendances.stream()
                    .filter(a -> "A".equals(a.getAttendance1())).count();

            presentCount += (int) attendances.stream()
                    .filter(a -> "P".equals(a.getAttendance2())).count();
            presentWithPermissionCount += (int) attendances.stream()
                    .filter(a -> "PA".equals(a.getAttendance2())).count();
            absentCount += (int) attendances.stream()
                    .filter(a -> "A".equals(a.getAttendance2())).count();

            double attendanceRatio = (double) (totalClasses - absentCount) / totalClasses;
            attendancePercentage = BigDecimal.valueOf(attendanceRatio * 100).setScale(2, RoundingMode.HALF_UP);
        }

        Optional<ExamDetail> theoreticalExamDetail = examDetailRepository.findByUserIdAndExamTypeAndSubjectId(student.getId(), MarkType.THEORETICAL, subjectId);
        Optional<ExamDetail> practicalExamDetail = examDetailRepository.findByUserIdAndExamTypeAndSubjectId(student.getId(), MarkType.PRACTICAL, subjectId);

        BigDecimal theoreticalScore = theoreticalExamDetail.map(ExamDetail::getScore).orElse(BigDecimal.ZERO);
        BigDecimal practicalScore = practicalExamDetail.map(ExamDetail::getScore).orElse(BigDecimal.ZERO);

        // Calculate percentage for each score
        BigDecimal theoreticalMaxScore = new BigDecimal("20"); // Assume maximum score for theoretical is 20
        BigDecimal practicalMaxScore = new BigDecimal("20");

        BigDecimal theoreticalPercentage = theoreticalScore.divide(theoreticalMaxScore, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        BigDecimal practicalPercentage = practicalScore.divide(practicalMaxScore, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));

        // Save or update StudentPerformance
        StudentPerformance performance;
        Optional<StudentPerformance> existingPerformance = studentPerformanceRepository.findByUserIdAndSubjectId(student.getId(), subjectId);

        if (existingPerformance.isPresent()) {
            performance = existingPerformance.get();
            performance.setAttendancePercentage(attendancePercentage);
            performance.setTheoryExamScore(theoreticalScore);
            performance.setPracticalExamScore(practicalScore);
            performance.setTheoreticalPercentage(theoreticalPercentage);
            performance.setPracticalPercentage(practicalPercentage);
            performance.setPresentCount(presentCount);
            performance.setPresentWithPermissionCount(presentWithPermissionCount);
            performance.setAbsentCount(absentCount);
        } else {
            performance = new StudentPerformance();
            performance.setUser(student);
            performance.setAttendancePercentage(attendancePercentage);
            performance.setTheoryExamScore(theoreticalScore);
            performance.setPracticalExamScore(practicalScore);
            performance.setTheoreticalPercentage(theoreticalPercentage);
            performance.setPracticalPercentage(practicalPercentage);
            performance.setPresentCount(presentCount);
            performance.setPresentWithPermissionCount(presentWithPermissionCount);
            performance.setAbsentCount(absentCount);
            performance.setSubject(subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new NotFoundException("Subject not found")));
            performance.setCreatedAt(LocalDateTime.now());
        }

        studentPerformanceRepository.save(performance);

        // Create response
        StudentPerformanceResponse response = new StudentPerformanceResponse();
        response.setStudentName(student.getUserDetail().getFullName());
        response.setSubjectCode(performance.getSubject().getSubjectCode());
        response.setTheoreticalScore(theoreticalScore);
        response.setPracticalScore(practicalScore);
        response.setAttendancePercentage(attendancePercentage);
        response.setTheoreticalPercentage(theoreticalPercentage);
        response.setPracticalPercentage(practicalPercentage);
        response.setPresentCount(presentCount);
        response.setPresentWithPermissionCount(presentWithPermissionCount);
        response.setAbsentCount(absentCount);

        return response;
    }

}
