package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.AssignTeacherRequest;
import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.*;
import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.entity.*;
import com.example.aptechstudentcaredserver.enums.MarkType;
import com.example.aptechstudentcaredserver.enums.Status;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.exception.EmptyListException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.*;
import com.example.aptechstudentcaredserver.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final GroupClassRepository groupClassRepository;
    private final UserCourseRepository userCourseRepository;
    private final CourseRepository courseRepository;
    private final CourseSubjectRepository courseSubjectRepository;
    private final SemesterRepository semesterRepository;
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final ExamDetailRepository examDetailRepository;

    private final UserSubjectRepository userSubjectRepository;
    private final StudentPerformanceRepository studentPerformanceRepository;

    @Override
    public List<ClassResponse> findAllClass() {
        List<Class> listClass = classRepository.findAll();
        if (listClass.isEmpty()) {
            throw new EmptyListException("No classes found.");
        }
        return listClass.stream()
                .map(this::convertToClassResponse)
                .collect(Collectors.toList());
    }


    public List<ClassResponse> getAllClassesByUser(User user) {
        List<Class> classes = groupClassRepository.findClassesByUser(user);
        return classes.stream()
                .map(this::convertToClassResponse)  // Gọi hàm convertToClassResponse
                .collect(Collectors.toList());      // Thu thập kết quả vào List
    }

    @Override
    public CourseWithClassesResponse findClassWithSubjectByClassId(int classId) {
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found with id: " + classId));

        Course course = existingClass.getCourse();
        if (course == null) {
            throw new NotFoundException("No course found for the class");
        }

        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(course.getId());

        Map<String, List<String>> subjectsBySemester = courseSubjects.stream()
                .collect(Collectors.groupingBy(
                        cs -> cs.getSemester().getName(),
                        Collectors.mapping(cs -> cs.getSubject().getSubjectCode(), Collectors.toList())
                ));

        return new CourseWithClassesResponse(
                existingClass.getId(),
                existingClass.getClassName(),
                course.getCourseName(),
                course.getCourseCode(),
                course.getCourseCompTime(),
                subjectsBySemester
        );
    }

    @Override
    public ClassResponse findClassById(int classId) {
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found with id: " + classId));

        return convertToClassResponse(existingClass);
    }

    @Override
    public Map<String, List<StudentPerformanceResponse>> getAllSubjectsBySemester(int classId, String semesterName, int userId) {
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found with id " + classId));

        Course course = existingClass.getCourse();
        if (course == null) {
            throw new NotFoundException("No course found for the class");
        }

        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(course.getId());

        if (semesterName != null && !semesterName.isEmpty()) {
            courseSubjects = courseSubjects.stream()
                    .filter(cs -> cs.getSemester().getName().equalsIgnoreCase(semesterName))
                    .collect(Collectors.toList());
            if (courseSubjects.isEmpty()) {
                throw new NotFoundException("No subjects found for the semester: " + semesterName);
            }
        }

        Map<String, List<StudentPerformanceResponse>> semesterSubjects = courseSubjects.stream()
                .collect(Collectors.groupingBy(
                        cs -> cs.getSemester().getName().toUpperCase(),
                        Collectors.mapping(cs -> {
                            StudentPerformanceResponse response = new StudentPerformanceResponse();
                            response.setId(cs.getSubject().getId());
                            response.setSubjectCode(cs.getSubject().getSubjectCode());

                            // Fetching attendance for the user
                            List<Attendance> attendances = attendanceRepository.findByUserId(userId);
                            List<Attendance> filteredAttendances = attendances.stream()
                                    .filter(a -> a.getSchedule().getClasses().getId() == classId && a.getSchedule().getSubject().getId() == cs.getSubject().getId())
                                    .toList();
                            Optional<User> user = userRepository.findById(userId);
                            if(user.isPresent()){
                                response.setStudentName(user.get().getUserDetail().getFullName());
                            }else{
                                response.setStudentName("Unknown studentName");
                            }

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

                            // Fetching performance data
                            Optional<StudentPerformance> performanceOpt = studentPerformanceRepository.findByUserIdAndSubjectId(userId, cs.getSubject().getId());
                            BigDecimal theoreticalPercentage = BigDecimal.ZERO;
                            BigDecimal practicalPercentage = BigDecimal.ZERO;
                            BigDecimal theoreticalScore = BigDecimal.ZERO;
                            BigDecimal practicalScore = BigDecimal.ZERO;

                            if (performanceOpt.isPresent()) {
                                StudentPerformance performance = performanceOpt.get();
                                theoreticalPercentage = performance.getTheoreticalPercentage();
                                practicalPercentage = performance.getPracticalPercentage();
                                theoreticalScore = performance.getTheoryExamScore();
                                practicalScore = performance.getPracticalExamScore();
                            } else {
                                // Fetch scores from ExamDetail if performance data not available
                                Optional<ExamDetail> theoreticalExamDetail = examDetailRepository.findByUserIdAndExamTypeAndSubjectId(userId, MarkType.THEORETICAL, cs.getSubject().getId());
                                Optional<ExamDetail> practicalExamDetail = examDetailRepository.findByUserIdAndExamTypeAndSubjectId(userId, MarkType.PRACTICAL, cs.getSubject().getId());

                                theoreticalScore = theoreticalExamDetail.map(ExamDetail::getScore).orElse(BigDecimal.ZERO);
                                practicalScore = practicalExamDetail.map(ExamDetail::getScore).orElse(BigDecimal.ZERO);

                                // Calculate percentages
                                BigDecimal theoreticalMaxScore = new BigDecimal("20");
                                BigDecimal practicalMaxScore = new BigDecimal("100");

                                // Calculate theoretical percentage
                                theoreticalPercentage = theoreticalScore.compareTo(BigDecimal.ZERO) > 0
                                        ? theoreticalScore.divide(theoreticalMaxScore, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                                        : BigDecimal.ZERO;

                                // Calculate practical percentage
                                if (practicalScore.compareTo(BigDecimal.valueOf(20)) <= 0) {
                                    practicalPercentage = practicalScore.divide(new BigDecimal("20"), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                                } else if (practicalScore.compareTo(BigDecimal.valueOf(21)) >= 0 && practicalScore.compareTo(practicalMaxScore) <= 0) {
                                    practicalPercentage = practicalScore;
                                } else {
                                    practicalPercentage = BigDecimal.ZERO;
                                }
                            }

                            // Set response fields
                            response.setTheoreticalPercentage(theoreticalPercentage);
                            response.setPracticalPercentage(practicalPercentage);
                            response.setTheoreticalScore(theoreticalScore);
                            response.setPracticalScore(practicalScore);
                            response.setPresentCount(presentCount);
                            response.setPresentWithPermissionCount(presentWithPermissionCount);
                            response.setAbsentCount(absentCount);
                            response.setAttendancePercentage(attendancePercentage);

                            return response;
                        }, Collectors.toList())
                ));

        return semesterSubjects;
    }

    @Override
    public void addClass(ClassRequest classRequest) {
        Class existingClass = classRepository.findByClassName(classRequest.getClassName());

        if (existingClass != null) {
            throw new DuplicateException("Class with this name already exists");
        }

        Class newClass = new Class();
        newClass.setClassName(classRequest.getClassName());
        newClass.setCenter(classRequest.getCenter());
        newClass.setStartHour(classRequest.getStartHour());
        newClass.setEndHour(classRequest.getEndHour());
        newClass.setDays(classRequest.getDays());
        newClass.setStatus(Status.STUDYING);

        Course course = courseRepository.findByCourseCode(classRequest.getCourseCode());
        if (course == null) {
            throw new NotFoundException("Course not found with code: " + classRequest.getCourseCode());
        }
        newClass.setCourse(course);

        Semester semester = semesterRepository.findByName("Sem1")
                .orElseThrow(() -> new NotFoundException("Default semester 'Sem1' not found."));
        newClass.setSemester(semester);

        newClass.setCreatedAt(LocalDateTime.now());
        newClass.setUpdatedAt(LocalDateTime.now());

        classRepository.save(newClass);
    }

    @Override
    public ClassResponse updateClass(int classId, ClassRequest classRequest) {
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found with id " + classId));

        existingClass.setClassName(classRequest.getClassName());
        existingClass.setCenter(classRequest.getCenter());
        existingClass.setStartHour(classRequest.getStartHour());
        existingClass.setEndHour(classRequest.getEndHour());
        existingClass.setDays(classRequest.getDays());
        existingClass.setStatus(Status.valueOf(classRequest.getStatus()));

        Course course = courseRepository.findByCourseCode(classRequest.getCourseCode());
        if (course == null) {
            throw new NotFoundException("Course not found with code: " + classRequest.getCourseCode());
        }
        existingClass.setCourse(course);

        if (classRequest.getSem() != null && !classRequest.getSem().isEmpty()) {
            Semester semester = semesterRepository.findByName(classRequest.getSem())
                    .orElseThrow(() -> new NotFoundException("Semester not found with name: " + classRequest.getSem()));
            existingClass.setSemester(semester);
        } else {
            existingClass.setSemester(null);
        }

        existingClass.setUpdatedAt(LocalDateTime.now());
        classRepository.save(existingClass);

        return convertToClassResponse(existingClass);
    }

    @Override
    public void deleteClass(int classId) {
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found with id " + classId));

        classRepository.delete(existingClass);
    }

    @Override
    public void assignTeacherToSubject(int classId, AssignTeacherRequest request) {
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));

        User newTeacher = userRepository.findByUserDetailFullName(request.getTeacherName());
        if (newTeacher == null || !newTeacher.getRole().getRoleName().equals("TEACHER")) {
            throw new RuntimeException("Invalid teacher");
        }

        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(existingClass.getCourse().getId());
        List<CourseSubject> filteredCourseSubjects = courseSubjects.stream()
                .filter(cs -> cs.getSubject().getSubjectCode().equals(request.getSubjectCode()))
                .collect(Collectors.toList());

        if (filteredCourseSubjects.isEmpty()) {
            throw new RuntimeException("Invalid subject or not assigned in the course");
        }

        // Lấy số giờ học từ lớp và môn học
        int totalClassHours = existingClass.getEndHour().getHour() - existingClass.getStartHour().getHour();
        int totalSubjectHours = filteredCourseSubjects.get(0).getSubject().getTotalHours(); 

        int numberOfSessions = (int) Math.ceil((double) totalSubjectHours / totalClassHours);

        List<UserSubject> userSubjectsForClass = userSubjectRepository.findByClassroom(existingClass);

        for (CourseSubject courseSubject : filteredCourseSubjects) {
            Subject subject = courseSubject.getSubject();

            Optional<UserSubject> existingUserSubject = userSubjectsForClass.stream()
                    .filter(us -> us.getSubject().equals(subject))
                    .findFirst();

            if (existingUserSubject.isPresent()) {
                UserSubject userSubject = existingUserSubject.get();
                userSubject.setUser(newTeacher);
                userSubject.setStatus(Status.valueOf(request.getStatus()));
                userSubject.setUpdatedAt(LocalDateTime.now());
                userSubject.setNumberOfSessions(numberOfSessions);
                userSubjectRepository.save(userSubject);
            } else {
                UserSubject newUserSubject = new UserSubject();
                newUserSubject.setUser(newTeacher);
                newUserSubject.setSubject(subject);
                newUserSubject.setClassroom(existingClass);
                newUserSubject.setCreatedAt(LocalDateTime.now());
                newUserSubject.setUpdatedAt(LocalDateTime.now());
                newUserSubject.setStatus(Status.ACTIVE);
                newUserSubject.setNumberOfSessions(numberOfSessions);
                userSubjectRepository.save(newUserSubject);
            }
        }
    }


    private ClassResponse convertToClassResponse(Class classEntity) {
        List<GroupClass> groupClasses = groupClassRepository.findByClassesId(classEntity.getId());
        Course course = classEntity.getCourse();
        CourseResponse courseResponse = null;

        List<CourseSubject> courseSubjects = course != null ? courseSubjectRepository.findByCourseId(course.getId()) : List.of();

        if (course != null) {
            String currentSemester = classEntity.getSemester() != null ? classEntity.getSemester().getName() : null;

            Map<String, List<String>> semesterSubjects = courseSubjects.stream()
                    .filter(cs -> cs.getSemester().getName().equals(currentSemester))
                    .collect(Collectors.groupingBy(
                            cs -> cs.getSemester().getName(),
                            Collectors.mapping(cs -> cs.getSubject().getSubjectCode(), Collectors.toList())
                    ));

            courseResponse = new CourseResponse(
                    course.getId(),
                    course.getCourseName(),
                    course.getCourseCode(),
                    course.getCourseCompTime(),
                    semesterSubjects
            );
        }

        String semesterName = classEntity.getSemester() != null ? classEntity.getSemester().getName() : null;

        List<StudentResponse> studentResponses = groupClasses.stream()
                .map(groupClass -> {
                    User user = groupClass.getUser();
                    List<String> courses = userCourseRepository.findByUserId(user.getId()).stream()
                            .map(userCourse -> userCourse.getCourse().getCourseName())
                            .collect(Collectors.toList());

                    return new StudentResponse(
                            user.getId(),
                            user.getUserDetail() != null ? user.getUserDetail().getImage() : null,
                            user.getUserDetail() != null ? user.getUserDetail().getRollNumber() : null,
                            user.getUserDetail() != null ? user.getUserDetail().getFullName() : null,
                            user.getEmail(),
                            user.getUserDetail() != null ? user.getUserDetail().getAddress() : null,
                            classEntity.getClassName(),
                            user.getUserDetail() != null ? user.getUserDetail().getGender() : null,
                            user.getUserDetail() != null ? user.getUserDetail().getDob() : null,
                            user.getUserDetail() != null ? user.getUserDetail().getPhone() : null,
                            courses,
                            groupClass.getStatus() != null ? groupClass.getStatus().name() : null,
                            user.getUserDetail() != null ? user.getUserDetail().getParent().getFullName() : null,
                            user.getUserDetail() != null ? user.getUserDetail().getParent().getStudentRelation() : null,
                            user.getUserDetail() != null ? user.getUserDetail().getParent().getPhone() : null,
                            user.getUserDetail() != null ? user.getUserDetail().getParent().getGender() : null
                    );
                })
                .collect(Collectors.toList());

        List<UserSubject> userSubjects = userSubjectRepository.findByClassroom(classEntity);

        List<SubjectTeacherResponse> subjectTeacherResponses = userSubjects.stream()
                .map(userSubject -> {
                    Subject subject = userSubject.getSubject();
                    User teacher = userSubject.getUser();
                    return new SubjectTeacherResponse(
                            subject.getId(),
                            teacher.getId(),
                            subject.getSubjectCode(),
                            teacher.getUserDetail().getFullName(),
                            userSubject.getStatus().name()
                    );
                })
                .collect(Collectors.toList());

        return new ClassResponse(
                classEntity.getId(),
                classEntity.getClassName(),
                classEntity.getCenter(),
                classEntity.getStartHour(),
                classEntity.getEndHour(),
                classEntity.getDays(),
                classEntity.getCreatedAt(),
                classEntity.getStatus().name(),
                semesterName,
                courseResponse,
                studentResponses,
                subjectTeacherResponses
        );
    }
}
