package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.CourseRequest;
import com.example.aptechstudentcaredserver.bean.response.CourseResponse;
import com.example.aptechstudentcaredserver.entity.Course;
import com.example.aptechstudentcaredserver.entity.CourseSubject;
import com.example.aptechstudentcaredserver.entity.Semester;
import com.example.aptechstudentcaredserver.entity.Subject;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.exception.EmptyListException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.CourseRepository;
import com.example.aptechstudentcaredserver.repository.CourseSubjectRepository;
import com.example.aptechstudentcaredserver.repository.SemesterRepository;
import com.example.aptechstudentcaredserver.repository.SubjectRepository;
import com.example.aptechstudentcaredserver.service.CourseService;
import com.example.aptechstudentcaredserver.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final SemesterService semesterService;

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
    public CourseResponse getCourseById(int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));

        return convertToCourseResponse(course);
    }

    @Override
    public void createCourse(CourseRequest request) {
        // Check if courseName already exists
        Course existCourseByName = courseRepository.findByCourseName(request.getCourseName());
        if (existCourseByName != null) {
            throw new DuplicateException("Course with name '" + request.getCourseName() + "' already exists");
        }

        // Check if courseCode already exists
        Course existCourseByCode = courseRepository.findByCourseCode(request.getCourseCode());
        if (existCourseByCode != null) {
            throw new DuplicateException("Course with code '" + request.getCourseCode() + "' already exists");
        }

        // Validate that at least one semester and subject are provided
        if (request.getSemesters() == null || request.getSemesters().isEmpty()) {
            throw new NotFoundException("At least one semester and subject must be provided");
        }

        // Validate that subjects exist in the system before adding them
        for (Map.Entry<String, List<String>> entry : request.getSemesters().entrySet()) {
            List<String> subjectNames = entry.getValue();

            if (subjectNames == null || subjectNames.isEmpty()) {
                throw new NotFoundException("Subjects for semester '" + entry.getKey() + "' must be provided");
            }

            for (String subjectName : subjectNames) {
                // Check if the subject exists in the database
                subjectRepository.findBySubjectName(subjectName)
                        .orElseThrow(() -> new NotFoundException("Subject '" + subjectName + "' not found in the system"));
            }
        }

        // If all validation passes, proceed with course creation
        Course course = new Course();
        course.setCourseName(request.getCourseName());
        course.setCourseCode(request.getCourseCode());
        course.setCourseCompTime(request.getCourseCompTime());
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());

        // Save the course
        course = courseRepository.save(course);

        // Process semesters and subjects
        processSemestersAndSubjects(request, course);
    }


    @Override
    public CourseResponse updateCourse(int courseId, CourseRequest request) {
        // Fetch the existing course by ID
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course with ID " + courseId + " not found"));

        // Check if courseName is being updated to an existing name for another course
        Course existCourseByName = courseRepository.findByCourseName(request.getCourseName());
        if (existCourseByName != null && existCourseByName.getId() != courseId) {
            // Throw DuplicateException with a message indicating the issue
            throw new DuplicateException("Course with name '" + request.getCourseName() + "' already exists.");
        }

        // Check if courseCode is being updated to an existing code for another course
        Course existCourseByCode = courseRepository.findByCourseCode(request.getCourseCode());
        if (existCourseByCode != null && existCourseByCode.getId() != courseId) {
            // Throw DuplicateException with a message indicating the issue
            throw new DuplicateException("Course with code '" + request.getCourseCode() + "' already exists.");
        }

        // Update course details (name, code, and compTime)
        course.setCourseName(request.getCourseName());
        course.setCourseCode(request.getCourseCode());
        course.setCourseCompTime(request.getCourseCompTime());
        course.setUpdatedAt(LocalDateTime.now());

        // Validate and update subjects only if they are provided in the request
        if (request.getSemesters() != null && !request.getSemesters().isEmpty()) {
            // Validate subjects
            for (Map.Entry<String, List<String>> entry : request.getSemesters().entrySet()) {
                List<String> subjectNames = entry.getValue();

                if (subjectNames == null || subjectNames.isEmpty()) {
                    // Throw exception with a message for missing subjects
                    throw new NotFoundException("Subjects for semester '" + entry.getKey() + "' must be provided.");
                }

                for (String subjectName : subjectNames) {
                    subjectRepository.findBySubjectName(subjectName)
                            .orElseThrow(() -> new NotFoundException("Subject '" + subjectName + "' not found in the system."));
                }
            }

            // If subjects are provided, remove existing course-subject relationships and update them
            List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(courseId);
            if (!courseSubjects.isEmpty()) {
                courseSubjectRepository.deleteAll(courseSubjects);
            }

            // Process and update semesters and subjects
            processSemestersAndSubjects(request, course);
        }

        // Save the updated course
        courseRepository.save(course);

        // Return the updated course response
        return convertToCourseResponse(course);
    }




    @Override
    public void deleteCourse(int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course with ID " + courseId + " not found"));

        // Delete between the course and the subjects
        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(courseId);

        if (!courseSubjects.isEmpty()) {
            courseSubjectRepository.deleteAll(courseSubjects);
        }

        courseRepository.delete(course);
    }

    private void processSemestersAndSubjects(CourseRequest request, Course course) {

        semesterService.initializeDefaultSemesters();
        for (Map.Entry<String, List<String>> entry : request.getSemesters().entrySet()) {
            String semesterKey = entry.getKey();
            List<String> subjectNames = entry.getValue();

            Semester semester = semesterRepository.findByName(semesterKey)
                    .orElseThrow(() -> new NotFoundException("Semester " + semesterKey + " not found"));

            for (String subjectName : subjectNames) {
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

    private CourseResponse convertToCourseResponse(Course course) {
        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(course.getId());

        Map<String, List<String>> semesterSubjects = courseSubjects.stream()
                .collect(Collectors.groupingBy(
                        cs -> cs.getSemester().getName(),  // Group by semester name
                        Collectors.mapping(cs -> cs.getSubject().getSubjectName(), Collectors.toList()) // Map subject names
                ));


        return new CourseResponse(
                course.getId(),
                course.getCourseName(),
                course.getCourseCode(),
                course.getCourseCompTime(),
                semesterSubjects
        );
    }
}
