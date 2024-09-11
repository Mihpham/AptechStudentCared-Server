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
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
//    private static final Logger log = (Logger) LoggerFactory.getLogger(CourseServiceImpl.class);

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
        Course existCourseByName = courseRepository.findByCourseName(request.getCourseName());
        if (existCourseByName != null) {
            throw new DuplicateException("Course with name '" + request.getCourseName() + "' already exists");
        }

        Course existCourseByCode = courseRepository.findByCourseCode(request.getCourseCode());
        if (existCourseByCode != null) {
            throw new DuplicateException("Course with code '" + request.getCourseCode() + "' already exists");
        }

        if (request.getSemesters() == null || request.getSemesters().isEmpty()) {
            throw new NotFoundException("At least one semester and subject must be provided");
        }

        // Create and save the course
        Course course = new Course();
        course.setCourseName(request.getCourseName());
        course.setCourseCode(request.getCourseCode());
        course.setCourseCompTime(request.getCourseCompTime());
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());

        course = courseRepository.save(course);

        // Process semesters and subjects
        List<CourseSubject> courseSubjectsToSave = new ArrayList<>();
        try {
            processSemestersAndSubjects(request, course, courseSubjectsToSave);
            // Save course subjects only after all are validated
            courseSubjectRepository.saveAll(courseSubjectsToSave);
        } catch (NotFoundException e) {
            // Rollback: Delete the course if there is an error
            courseRepository.delete(course);
            throw e; // Re-throw the exception after rollback
        }
    }

    @Override
    public CourseResponse updateCourse(int courseId, CourseRequest request) {
        // Fetch the existing course by ID
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course with ID " + courseId + " not found"));

        // Check for duplicate course name or code
        Course existCourseByName = courseRepository.findByCourseName(request.getCourseName());
        if (existCourseByName != null && existCourseByName.getId() != courseId) {
            throw new DuplicateException("Course with name '" + request.getCourseName() + "' already exists.");
        }

        Course existCourseByCode = courseRepository.findByCourseCode(request.getCourseCode());
        if (existCourseByCode != null && existCourseByCode.getId() != courseId) {
            throw new DuplicateException("Course with code '" + request.getCourseCode() + "' already exists.");
        }

        // Update course details
        course.setCourseName(request.getCourseName());
        course.setCourseCode(request.getCourseCode());
        course.setCourseCompTime(request.getCourseCompTime());
        course.setUpdatedAt(LocalDateTime.now());

        // Save the updated course
        courseRepository.save(course);

        // Process semesters and subjects
        List<CourseSubject> courseSubjectsToSave = new ArrayList<>();
        List<CourseSubject> courseSubjectsToDelete = courseSubjectRepository.findByCourseId(courseId);

        // Collect subjects to delete based on new data
        for (CourseSubject courseSubject : courseSubjectsToDelete) {
            if (!request.getSemesters().containsKey(courseSubject.getSemester().getName()) ||
                    !request.getSemesters().get(courseSubject.getSemester().getName()).contains(courseSubject.getSubject().getSubjectCode())) {
                courseSubjectsToDelete.remove(courseSubject);
            }
        }
        courseSubjectRepository.deleteAll(courseSubjectsToDelete);

        try {
            processSemestersAndSubjects(request, course, courseSubjectsToSave);
            // Save course subjects only after all are validated
            courseSubjectRepository.saveAll(courseSubjectsToSave);
        } catch (NotFoundException e) {
            // Handle the case where subjects or semesters are not found
            // Just log the error or handle it as per your requirements
            // Do not delete the course here
            throw e; // Re-throw the exception
        }

        return convertToCourseResponse(course);
    }


    @Override
    public void deleteCourse(int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course with ID " + courseId + " not found"));

        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(courseId);

        if (!courseSubjects.isEmpty()) {
            courseSubjectRepository.deleteAll(courseSubjects);
        }

        courseRepository.delete(course);
    }


    private void processSemestersAndSubjects(CourseRequest request, Course course, List<CourseSubject> courseSubjectsToSave) {
        semesterService.initializeDefaultSemesters();
        for (Map.Entry<String, List<String>> entry : request.getSemesters().entrySet()) {
            String semesterKey = entry.getKey();
            List<String> subjectCodes = entry.getValue();

            Semester semester = semesterRepository.findByName(semesterKey)
                    .orElseThrow(() -> new NotFoundException("Semester " + semesterKey + " not found"));

            for (String subjectCode : subjectCodes) {
                Subject subject = subjectRepository.findBySubjectCode(subjectCode)
                        .orElseThrow(() -> new NotFoundException("Subject " + subjectCode + " not found"));

                CourseSubject courseSubject = new CourseSubject();
                courseSubject.setCourse(course);
                courseSubject.setSubject(subject);
                courseSubject.setSemester(semester);

                courseSubjectsToSave.add(courseSubject);
            }
        }
    }

    private CourseResponse convertToCourseResponse(Course course) {
        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(course.getId());

        Map<String, List<String>> semesterSubjects = courseSubjects.stream()
                .collect(Collectors.groupingBy(
                        cs -> cs.getSemester().getName(),
                        Collectors.mapping(cs -> cs.getSubject().getSubjectName(), Collectors.toList())
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
