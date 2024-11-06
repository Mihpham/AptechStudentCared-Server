package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.response.StudentPerformanceResponse;
import com.example.aptechstudentcaredserver.bean.response.SubjectPerformance;
import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.entity.*;
import com.example.aptechstudentcaredserver.enums.MarkType;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.*;
import com.example.aptechstudentcaredserver.service.StudentPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentPerformanceServiceImpl implements StudentPerformanceService {

    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final ExamDetailRepository examDetailRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentPerformanceRepository studentPerformanceRepository;
    private final SubjectRepository subjectRepository;
    private final CourseSubjectRepository courseSubjectRepository;
    private final ScheduleRepository scheduleRepository;


    @Override
    public SubjectPerformance saveStudentPerformance(int userId, int subjectId, int classId) {
        User student = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Student not found with id " + userId));

        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found with id " + classId));

        // Lấy attendance cho userId
        List<Attendance> attendances = attendanceRepository.findByUserId(userId);

        // Lọc attendance theo classId và subjectId
        List<Attendance> filteredAttendances = attendances.stream()
                .filter(a -> a.getSchedule().getClasses().getId() == classId && a.getSchedule().getSubject().getId() == subjectId)
                .toList();

        long totalClasses = filteredAttendances.size();

        int presentCount = 0;
        int presentWithPermissionCount = 0;
        int absentCount = 0;
        BigDecimal attendancePercentage = BigDecimal.ZERO;

        if (totalClasses > 0) {
            // Tính toán attendance types
            presentCount = (int) filteredAttendances.stream()
                    .filter(a -> "P".equals(a.getAttendance1())).count();
            presentWithPermissionCount = (int) filteredAttendances.stream()
                    .filter(a -> "PA".equals(a.getAttendance1())).count();
            absentCount = (int) filteredAttendances.stream()
                    .filter(a -> "A".equals(a.getAttendance1())).count();

            presentCount += (int) filteredAttendances.stream()
                    .filter(a -> "P".equals(a.getAttendance2())).count();
            presentWithPermissionCount += (int) filteredAttendances.stream()
                    .filter(a -> "PA".equals(a.getAttendance2())).count();
            absentCount += (int) filteredAttendances.stream()
                    .filter(a -> "A".equals(a.getAttendance2())).count();

            double attendanceRatio = (double) (totalClasses - absentCount) / totalClasses;
            attendancePercentage = BigDecimal.valueOf(attendanceRatio * 100).setScale(2, RoundingMode.HALF_UP);
        }

        Optional<ExamDetail> theoreticalExamDetail = examDetailRepository.findByUserIdAndExamTypeAndSubjectId(student.getId(), MarkType.THEORETICAL, subjectId);
        Optional<ExamDetail> practicalExamDetail = examDetailRepository.findByUserIdAndExamTypeAndSubjectId(student.getId(), MarkType.PRACTICAL, subjectId);

        BigDecimal theoreticalScore = theoreticalExamDetail.map(ExamDetail::getScore).orElse(BigDecimal.ZERO);
        BigDecimal practicalScore = practicalExamDetail.map(ExamDetail::getScore).orElse(BigDecimal.ZERO);

        // Tính tỷ lệ phần trăm cho từng điểm số
        BigDecimal theoreticalMaxScore = new BigDecimal("20"); // Giả định điểm tối đa cho lý thuyết là 20
        BigDecimal practicalMaxScore = new BigDecimal("20");

        BigDecimal theoreticalPercentage = theoreticalScore.divide(theoreticalMaxScore, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        BigDecimal practicalPercentage = practicalScore.divide(practicalMaxScore, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));

        // Lưu hoặc cập nhật StudentPerformance
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

        // Tạo response
        SubjectPerformance response = new SubjectPerformance();
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

    @Override
    public StudentPerformanceResponse getAllSubjectsBySemester(int classId, String semesterName, int userId) {
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found with id " + classId));

        Course course = existingClass.getCourse();
        if (course == null) {
            throw new NotFoundException("No course found for the class");
        }

        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(course.getId());

        // Lọc môn học theo kỳ nếu có thông tin semesterName
        if (semesterName != null && !semesterName.isEmpty()) {
            courseSubjects = courseSubjects.stream()
                    .filter(cs -> cs.getSemester().getName().equalsIgnoreCase(semesterName))
                    .collect(Collectors.toList());
            if (courseSubjects.isEmpty()) {
                throw new NotFoundException("No subjects found for the semester: " + semesterName);
            }
        }

        // Danh sách để chứa kết quả của các môn học
        List<SubjectPerformance> performances = new ArrayList<>();

        LocalDate firstSubjectSchedule = null;
        LocalDate lastSubjectSchedule = null;

        // Duyệt qua tất cả các môn học
        for (CourseSubject cs : courseSubjects) {
            SubjectPerformance response = new SubjectPerformance();
            response.setId(cs.getSubject().getId());
            response.setSubjectCode(cs.getSubject().getSubjectCode());

            // Lấy thông tin về điểm danh của sinh viên
            List<Attendance> attendances = attendanceRepository.findByUserId(userId);
            List<Attendance> filteredAttendances = attendances.stream()
                    .filter(a -> a.getSchedule().getClasses().getId() == classId && a.getSchedule().getSubject().getId() == cs.getSubject().getId())
                    .collect(Collectors.toList());

            Optional<User> user = userRepository.findById(userId);
            response.setStudentName(user.map(u -> u.getUserDetail().getFullName()).orElse("Unknown studentName"));

            long totalClasses = filteredAttendances.size();
            int presentCount = (int) filteredAttendances.stream().filter(a -> "P".equals(a.getAttendance1())).count() +
                    (int) filteredAttendances.stream().filter(a -> "P".equals(a.getAttendance2())).count();
            int presentWithPermissionCount = (int) filteredAttendances.stream().filter(a -> "PA".equals(a.getAttendance1())).count() +
                    (int) filteredAttendances.stream().filter(a -> "PA".equals(a.getAttendance2())).count();
            int absentCount = (int) filteredAttendances.stream().filter(a -> "A".equals(a.getAttendance1())).count() +
                    (int) filteredAttendances.stream().filter(a -> "A".equals(a.getAttendance2())).count();

            BigDecimal attendancePercentage = totalClasses > 0
                    ? BigDecimal.valueOf((double) (totalClasses - absentCount) / totalClasses * 100).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            // Lấy điểm thi lý thuyết và thực hành từ ExamDetail
            Optional<ExamDetail> theoreticalExamDetail = examDetailRepository.findByUserIdAndExamTypeAndSubjectId(userId, MarkType.THEORETICAL, cs.getSubject().getId());
            Optional<ExamDetail> practicalExamDetail = examDetailRepository.findByUserIdAndExamTypeAndSubjectId(userId, MarkType.PRACTICAL, cs.getSubject().getId());

            BigDecimal theoreticalScore = BigDecimal.ZERO;
            BigDecimal practicalScore = BigDecimal.ZERO;
            BigDecimal theoreticalPercentage = BigDecimal.ZERO;
            BigDecimal practicalPercentage = BigDecimal.ZERO;

            // Kiểm tra điểm thi lý thuyết và thực hành
            if (theoreticalExamDetail.isPresent()) {
                theoreticalScore = theoreticalExamDetail.get().getScore();
            }

            if (practicalExamDetail.isPresent()) {
                practicalScore = practicalExamDetail.get().getScore();
            }

            // Tính toán tỷ lệ phần trăm cho lý thuyết và thực hành
            BigDecimal theoreticalMaxScore = new BigDecimal("20");
            BigDecimal practicalMaxScore = new BigDecimal("100");

            theoreticalPercentage = theoreticalScore.compareTo(BigDecimal.ZERO) > 0
                    ? theoreticalScore.divide(theoreticalMaxScore, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;

            if (practicalScore.compareTo(BigDecimal.ZERO) > 0) {
                practicalPercentage = practicalScore.compareTo(BigDecimal.valueOf(20)) <= 0
                        ? practicalScore.divide(new BigDecimal("20"), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                        : practicalScore.compareTo(practicalMaxScore) <= 0
                        ? practicalScore
                        : BigDecimal.ZERO;
            }

            // Cập nhật thông tin vào response
            response.setTheoreticalPercentage(theoreticalPercentage);
            response.setPracticalPercentage(practicalPercentage);
            response.setTheoreticalScore(theoreticalScore);
            response.setPracticalScore(practicalScore);
            response.setPresentCount(presentCount);
            response.setPresentWithPermissionCount(presentWithPermissionCount);
            response.setAbsentCount(absentCount);
            response.setAttendancePercentage(attendancePercentage);

            performances.add(response);

            // Cập nhật lịch học đầu tiên và cuối cùng
            List<Schedule> schedules = scheduleRepository.findBySubjectId(cs.getSubject().getId());
            if (!schedules.isEmpty()) {
                LocalDate firstSchedule = schedules.get(0).getStartDate();
                LocalDate lastSchedule = schedules.get(schedules.size() - 1).getEndDate();
                if (firstSubjectSchedule == null || firstSchedule.isBefore(firstSubjectSchedule)) {
                    firstSubjectSchedule = firstSchedule;
                }
                if (lastSubjectSchedule == null || lastSchedule.isAfter(lastSubjectSchedule)) {
                    lastSubjectSchedule = lastSchedule;
                }
            }

            // Tạo hoặc cập nhật StudentPerformance
            Optional<StudentPerformance> existingPerformance = studentPerformanceRepository.findByUserIdAndSubjectId(userId, cs.getSubject().getId());
            StudentPerformance studentPerformance;

            if (existingPerformance.isPresent()) {
                studentPerformance = existingPerformance.get();
            } else {
                studentPerformance = new StudentPerformance();
                studentPerformance.setUser(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found")));
                studentPerformance.setSubject(cs.getSubject());
            }

            // Cập nhật điểm và tỷ lệ phần trăm vào StudentPerformance
            studentPerformance.setPresentCount(presentCount);
            studentPerformance.setPresentWithPermissionCount(presentWithPermissionCount);
            studentPerformance.setAbsentCount(absentCount);
            studentPerformance.setTheoryExamScore(theoreticalScore);
            studentPerformance.setPracticalExamScore(practicalScore);
            studentPerformance.setAttendancePercentage(attendancePercentage);
            studentPerformance.setPracticalPercentage(practicalPercentage);
            studentPerformance.setTheoreticalPercentage(theoreticalPercentage);
            studentPerformance.setCreatedAt(LocalDateTime.now());

            studentPerformance.setFirstSubjectSchedules(firstSubjectSchedule != null ? firstSubjectSchedule.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : null);
            studentPerformance.setLastSubjectSchedules(lastSubjectSchedule != null ? lastSubjectSchedule.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : null);

            studentPerformanceRepository.save(studentPerformance);
        }

        StudentPerformanceResponse response = new StudentPerformanceResponse();
        response.setFirstSubjectSchedules(firstSubjectSchedule != null ? firstSubjectSchedule.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : null);
        response.setLastSubjectSchedules(lastSubjectSchedule != null ? lastSubjectSchedule.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : null);
        response.setSubjectPerformances(performances);

        return response;
    }
}
