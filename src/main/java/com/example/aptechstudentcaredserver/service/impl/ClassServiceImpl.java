package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
import com.example.aptechstudentcaredserver.bean.response.CourseResponse;
import com.example.aptechstudentcaredserver.bean.response.StudentResponse;
import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.entity.*;
import com.example.aptechstudentcaredserver.enums.Status;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.exception.EmptyListException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.*;
import com.example.aptechstudentcaredserver.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    private final UserRepository userRepository;
    private final UserSubjectRepository userSubjectRepository;
    
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

    @Override
    public ClassResponse findClassById(int classId) {
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found with id: " + classId));

        return convertToClassResponse(existingClass);
    }

    @Override
    public Map<String, List<String>> getAllSubjectsBySemester(int classId, String semesterName) {
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

        Map<String, List<String>> semesterSubjects = courseSubjects.stream()
                .collect(Collectors.groupingBy(
                        cs -> cs.getSemester().getName().toUpperCase(), // Convert semester name to uppercase
                        Collectors.mapping(cs -> cs.getSubject().getSubjectName(), Collectors.toList()) // Map to subject names
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
        newClass.setHour(classRequest.getHour());
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
        existingClass.setHour(classRequest.getHour());
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
    public void assignTeacherToSubject(int classId, String subjectCode, String teacherName) {
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));

        User newTeacher = userRepository.findByUserDetailFullName(teacherName);
        if (newTeacher == null || !newTeacher.getRole().getRoleName().equals("TEACHER")) {
            throw new RuntimeException("Invalid teacher");
        }

        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(existingClass.getCourse().getId());
        List<CourseSubject> filteredCourseSubjects = courseSubjects.stream()
                .filter(cs -> cs.getSubject().getSubjectCode().equals(subjectCode))
                .collect(Collectors.toList());

        if (filteredCourseSubjects.isEmpty()) {
            throw new RuntimeException("Invalid subject or not assigned in the course");
        }

        List<UserSubject> userSubjectsForClass = userSubjectRepository.findByClassroom(existingClass);

        for (CourseSubject courseSubject : filteredCourseSubjects) {
            Subject subject = courseSubject.getSubject();

            Optional<UserSubject> existingUserSubject = userSubjectsForClass.stream()
                    .filter(us -> us.getSubject().equals(subject))
                    .findFirst();

            if (existingUserSubject.isPresent()) {
                UserSubject userSubject = existingUserSubject.get();
                userSubject.setUser(newTeacher);
                userSubject.setUpdatedAt(LocalDateTime.now());
                userSubjectRepository.save(userSubject);
            } else {
                UserSubject newUserSubject = new UserSubject();
                newUserSubject.setUser(newTeacher);
                newUserSubject.setSubject(subject);
                newUserSubject.setClassroom(existingClass);
                newUserSubject.setCreatedAt(LocalDateTime.now());
                newUserSubject.setUpdatedAt(LocalDateTime.now());
                userSubjectRepository.save(newUserSubject);
            }
        }
    }

    private ClassResponse convertToClassResponse(Class classEntity) {
        List<GroupClass> groupClasses = groupClassRepository.findByClassesId(classEntity.getId());
        Course course = classEntity.getCourse();
        CourseResponse courseResponse = null;

        // Lấy danh sách các môn học của khóa học
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


        // Chỉ lấy UserSubject cho lớp hiện tại
        List<UserSubject> userSubjects = userSubjectRepository.findByClassroom(classEntity);

        Map<String, String> subjectTeacherMap = userSubjects.stream()
                .collect(Collectors.toMap(
                        userSubject -> userSubject.getSubject().getSubjectCode(),
                        userSubject -> userSubject.getUser().getUserDetail().getFullName()
                ));

        return new ClassResponse(
                classEntity.getId(),
                classEntity.getClassName(),
                classEntity.getCenter(),
                classEntity.getHour(),
                classEntity.getDays(),
                classEntity.getCreatedAt(),
                classEntity.getStatus().name(),
                semesterName,
                courseResponse,
                studentResponses,
                subjectTeacherMap
        );
    }
}
