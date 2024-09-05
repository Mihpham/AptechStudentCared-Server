package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.CourseRequest;
import com.example.aptechstudentcaredserver.bean.response.CourseResponse;
import com.example.aptechstudentcaredserver.entity.Course;
import com.example.aptechstudentcaredserver.entity.CourseSubject;
import com.example.aptechstudentcaredserver.entity.Semester;
import com.example.aptechstudentcaredserver.entity.Subject;
import com.example.aptechstudentcaredserver.exception.EmptyListException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.CourseRepository;
import com.example.aptechstudentcaredserver.repository.CourseSubjectRepository;
import com.example.aptechstudentcaredserver.repository.SemesterRepository;
import com.example.aptechstudentcaredserver.repository.SubjectRepository;
import com.example.aptechstudentcaredserver.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    private final CourseSubjectRepository courseSubjectRepository;

    @Override
    public List<CourseResponse> getAllCourses() {
        try {
            List<Course> courses = courseRepository.findAll();
            if (courses.isEmpty()) {
                throw new EmptyListException("No course found.");
            }
            return courseRepository.findAll().stream()
                    .map(this::convertToCourseResponse)
                    .collect(Collectors.toList());
        } catch (EmptyListException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve course.", e);
        }
    }

    @Override
    public CourseResponse getCourseById(int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));
        return convertToCourseResponse(course);
    }

    @Override
    public void createCourse(CourseRequest request) {
        // Step 1: Save Course
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

        // Step 2: Process Semesters and Subjects
        for (Map.Entry<String, List<String>> entry : request.getSemesters().entrySet()) {
            String semesterKey = entry.getKey();
            List<String> subjectNames = entry.getValue();

            // Get or Create Semester
            Semester semester = semesterRepository.findByName(semesterKey)
                    .orElseGet(() -> createSemester(semesterKey));

            for (String subjectName : subjectNames) {
                // Get or Create Subject
                Subject subject = subjectRepository.findBySubjectName(subjectName)
                        .orElseGet(() -> createSubject(subjectName));

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
        semester.setStartDate("2024-01-01"); // Replace with your default start date
        semester.setEndDate("2024-06-30");   // Replace with your default end date
        return semesterRepository.save(semester);
    }
    private Subject createSubject(String name) {
        Subject subject = new Subject();
        subject.setSubjectName(name);
        subject.setSubjectCode("DEFAULT_CODE"); // Replace with default or generate codes
        subject.setTotalHours(0); // Replace with default or calculated hours
        subject.setCreatedAt(LocalDateTime.now());
        subject.setUpdatedAt(LocalDateTime.now());
        return subjectRepository.save(subject);
    }

//    @Override
//    public CourseResponse updateCourse(int courseId, CourseRequest courseRequest) {
//        try {
//            Course course = courseRepository.findById(courseId)
//                    .orElseThrow(() -> new NotFoundException("Course with " + courseId + " not found"));
//
//            course.setCourseName(courseRequest.getCourseName());
//            course.setCourseCode(courseRequest.getCourseCode());
//            course.setCourseCompTime(courseRequest.getCourseCompTime());
//            course.setUpdatedAt(LocalDateTime.now());
//
//            // Clear old course-subject relations
//            course.getCourseSubjects().clear();
//            courseSubjectRepository.deleteById(courseId);
//
//            // Add new subjects per semester
//            addSubjectsToCourse(course, courseRequest.getSubjectsPerSemester());
//
//            Course updatedCourse = courseRepository.save(course);
//            return convertToCourseResponse(updatedCourse);
//        } catch (NotFoundException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to update course.", e);
//        }
//
//    }

    @Override
    public void deleteCourse(int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course with " + courseId + " not found"));
        courseRepository.delete(course);
    }

    private CourseResponse convertToCourseResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getCourseName(),
                course.getCourseCode(),
                course.getClassSchedule(),
                course.getCourseCompTime()
        );
    }
}
