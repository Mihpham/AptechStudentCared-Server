package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.CourseRequest;
import com.example.aptechstudentcaredserver.bean.response.CourseResponse;
import com.example.aptechstudentcaredserver.bean.response.SubjectResponse;
import com.example.aptechstudentcaredserver.entity.*;
import com.example.aptechstudentcaredserver.exception.EmptyListException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.*;
import com.example.aptechstudentcaredserver.service.CourseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    private final CourseSubjectRepository courseSubjectRepository;
    private final UserCourseRepository userCourseRepository;

    @Override
    public CourseResponse getCourseById(int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));

        // Lấy danh sách các môn học liên quan đến khóa học này
        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(courseId);
        List<SubjectResponse> subjectResponses = courseSubjects.stream()
                .map(cs -> {
                    Subject subject = cs.getSubject();
                    return new SubjectResponse(
                            subject.getId(),
                            subject.getSubjectName(),
                            subject.getSubjectCode(),
                            subject.getTotalHours(),
                            subject.getCreatedAt(),
                            subject.getUpdatedAt()
                    );
                })
                .collect(Collectors.toList());

        return new CourseResponse(
                course.getId(),
                course.getCourseName(),
                course.getCourseCode(),
                course.getClassSchedule(),
                course.getCourseCompTime(),
                subjectResponses
        );
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        if (courses.isEmpty()) {
            throw new EmptyListException("No course found.");
        }
        return courses.stream()
                .map(this::convertToCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void createCourse(CourseRequest request) {
        // Save Course
        Course course = new Course();
        course.setCourseName(request.getCourseName());
        course.setCourseCode(request.getCourseCode());
        course.setClassSchedule(request.getClassSchedule());
        course.setCourseCompTime(request.getCourseCompTime());

        LocalDateTime now = LocalDateTime.now();
        course.setCreatedAt(now);
        course.setUpdatedAt(now);

        course = courseRepository.save(course);

        // Initialize default semesters
        initializeDefaultSemesters();

        // Process Semesters and Subjects
        for (Map.Entry<String, List<String>> entry : request.getSemesters().entrySet()) {
            String semesterKey = entry.getKey();
            List<String> subjectNames = entry.getValue();

            // Get or Create Semester
            Semester semester = semesterRepository.findByName(semesterKey)
                    .orElseThrow(() -> new NotFoundException("Semester " + semesterKey + " not found"));

            for (String subjectName : subjectNames) {
                // Check if Subject exists
                Subject subject = subjectRepository.findBySubjectName(subjectName)
                        .orElseThrow(() -> new NotFoundException("Subject " + subjectName + " not found"));

                CourseSubject courseSubject = new CourseSubject();
                courseSubject.setCourse(course);
                courseSubject.setSubject(subject);
                courseSubject.setSemester(semester);

                courseSubjectRepository.save(courseSubject);
            }
        }
    }

    private void initializeDefaultSemesters() {
        String[] semesterNames = {"sem1", "sem2", "sem3"};
        for (String name : semesterNames) {
            semesterRepository.findByName(name)
                    .orElseGet(() -> createSemester(name));
        }
    }

    private Semester createSemester(String name) {
        Semester semester = new Semester();
        semester.setName(name);
        semester.setStartDate("2024-01-01"); // Default start date
        semester.setEndDate("2024-06-30");   // Default end date
        return semesterRepository.save(semester);
    }

    @Transactional
    @Override
    public CourseResponse updateCourse(int courseId, CourseRequest request) {
        // Step 1: Find the course by ID or throw an exception if not found
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course with id " + courseId + " not found"));

        // Step 2: Update the course fields with the new values from the request
        course.setCourseName(request.getCourseName());
        course.setCourseCode(request.getCourseCode());
        course.setClassSchedule(request.getClassSchedule());
        course.setCourseCompTime(request.getCourseCompTime());
        course.setUpdatedAt(LocalDateTime.now());

        // Step 3: Handle the semester and subject update logic
        // Remove all current course-subject relations before updating
        courseSubjectRepository.deleteByCourseId(courseId);

        // Initialize default semesters if they don't already exist
        initializeDefaultSemesters();

        // Process Semesters and Subjects
        for (Map.Entry<String, List<String>> entry : request.getSemesters().entrySet()) {
            String semesterKey = entry.getKey();
            List<String> subjectNames = entry.getValue();

            // Find the semester by name or throw an exception if not found
            Semester semester = semesterRepository.findByName(semesterKey)
                    .orElseThrow(() -> new NotFoundException("Semester " + semesterKey + " not found"));

            // For each subject, update the course-subject relationship
            for (String subjectName : subjectNames) {
                Subject subject = subjectRepository.findBySubjectName(subjectName)
                        .orElseThrow(() -> new NotFoundException("Subject " + subjectName + " not found"));

                // Create new CourseSubject relationships
                CourseSubject courseSubject = new CourseSubject();
                courseSubject.setCourse(course);
                courseSubject.setSubject(subject);
                courseSubject.setSemester(semester);

                courseSubjectRepository.save(courseSubject);
            }
        }

        // Step 4: Save the updated course
        courseRepository.save(course);

        // Step 5: Return the updated course as a response
        return convertToCourseResponse(course);
    }

    @Transactional
    @Override
    public void deleteCourse(int courseId) {
        // Tìm khóa học theo ID
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course with ID " + courseId + " not found"));

        // Xóa các liên kết giữa khóa học và các môn học
        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(courseId);
        if (!courseSubjects.isEmpty()) {
            courseSubjectRepository.deleteAll(courseSubjects);
        }

        // Xóa khóa học
        courseRepository.delete(course);
    }


    private CourseResponse convertToCourseResponse(Course course) {
        // Lấy danh sách các môn học liên quan đến khóa học này
        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(course.getId());
        List<SubjectResponse> subjectResponses = courseSubjects.stream()
                .map(cs -> {
                    Subject subject = cs.getSubject();
                    return new SubjectResponse(
                            subject.getId(),
                            subject.getSubjectName(),
                            subject.getSubjectCode(),
                            subject.getTotalHours(),
                            subject.getCreatedAt(),
                            subject.getUpdatedAt()
                    );
                })
                .collect(Collectors.toList());

        return new CourseResponse(
                course.getId(),
                course.getCourseName(),
                course.getCourseCode(),
                course.getClassSchedule(),
                course.getCourseCompTime(),
                subjectResponses
        );
    }
}
