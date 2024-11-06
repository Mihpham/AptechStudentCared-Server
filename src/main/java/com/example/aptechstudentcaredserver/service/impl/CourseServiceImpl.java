package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.CourseRequest;
import com.example.aptechstudentcaredserver.bean.response.CourseResponse;
import com.example.aptechstudentcaredserver.entity.Course;
import com.example.aptechstudentcaredserver.entity.CourseSubject;
import com.example.aptechstudentcaredserver.entity.Semester;
import com.example.aptechstudentcaredserver.entity.Subject;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.CourseRepository;
import com.example.aptechstudentcaredserver.repository.CourseSubjectRepository;
import com.example.aptechstudentcaredserver.repository.SemesterRepository;
import com.example.aptechstudentcaredserver.repository.SubjectRepository;
import com.example.aptechstudentcaredserver.service.CourseService;
import com.example.aptechstudentcaredserver.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
    public List<CourseResponse> getAllCourses(Pageable pageable) {
       Page<Course> courses = courseRepository.findAll(pageable);
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

        Course course = new Course();
        course.setCourseName(request.getCourseName());
        course.setCourseCode(request.getCourseCode());
        course.setCourseCompTime(request.getCourseCompTime());
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());

        course = courseRepository.save(course);
        saveCourseSubjects(request, course);
    }

    @Override
    public CourseResponse updateCourse(int courseId, CourseRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course with ID " + courseId + " not found"));

        checkForDuplicates(request, courseId);

        course.setCourseName(request.getCourseName());
        course.setCourseCode(request.getCourseCode());
        course.setCourseCompTime(request.getCourseCompTime());
        course.setUpdatedAt(LocalDateTime.now());

        courseRepository.save(course);

        List<CourseSubject> existingCourseSubjects = courseSubjectRepository.findByCourseId(courseId);
        Map<String, List<String>> existingSubjectsBySemester = existingCourseSubjects.stream()
                .collect(Collectors.groupingBy(
                        cs -> cs.getSemester().getName(),
                        Collectors.mapping(cs -> cs.getSubject().getSubjectCode(), Collectors.toList())
                ));

        List<CourseSubject> courseSubjectsToSave = new ArrayList<>();
        List<CourseSubject> courseSubjectsToDelete = new ArrayList<>(existingCourseSubjects);

        for (Map.Entry<String, List<String>> entry : request.getSemesters().entrySet()) {
            String semesterKey = entry.getKey();
            List<String> subjectCodes = entry.getValue();

            Semester semester = semesterRepository.findByName(semesterKey)
                    .orElseThrow(() -> new NotFoundException("Semester " + semesterKey + " not found"));

            List<String> addedSubjects = new ArrayList<>();

            for (String subjectCode : subjectCodes) {
                Subject subject = subjectRepository.findBySubjectCode(subjectCode)
                        .orElseThrow(() -> new NotFoundException("Subject " + subjectCode + " not found"));

                if (addedSubjects.contains(subjectCode)) {
                    throw new DuplicateException("Subject '" + subjectCode + "' already exists in " + semesterKey);
                }
                addedSubjects.add(subjectCode);

                if (!existingSubjectsBySemester.getOrDefault(semesterKey, Collections.emptyList()).contains(subjectCode)) {
                    CourseSubject courseSubject = new CourseSubject();
                    courseSubject.setCourse(course);
                    courseSubject.setSubject(subject);
                    courseSubject.setSemester(semester);
                    courseSubjectsToSave.add(courseSubject);
                } else {
                    courseSubjectsToDelete.removeIf(cs -> cs.getSubject().getSubjectCode().equals(subjectCode) && cs.getSemester().getName().equals(semesterKey));
                }
            }
        }

        courseSubjectRepository.saveAll(courseSubjectsToSave);
        courseSubjectRepository.deleteAll(courseSubjectsToDelete);

        return convertToCourseResponse(course);
    }


    private void checkForDuplicates(CourseRequest request, int courseId) {
        Course existCourseByName = courseRepository.findByCourseName(request.getCourseName());
        if (existCourseByName != null && existCourseByName.getId() != courseId) {
            throw new DuplicateException("Course with name '" + request.getCourseName() + "' already exists.");
        }

        Course existCourseByCode = courseRepository.findByCourseCode(request.getCourseCode());
        if (existCourseByCode != null && existCourseByCode.getId() != courseId) {
            throw new DuplicateException("Course with code '" + request.getCourseCode() + "' already exists.");
        }
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

    private void processSemestersAndSubjects(CourseRequest request, Course course, List<CourseSubject> courseSubjectsToSave, boolean isUpdate) {
        semesterService.initializeDefaultSemesters();
        for (Map.Entry<String, List<String>> entry : request.getSemesters().entrySet()) {
            String semesterKey = entry.getKey();
            List<String> subjectCodes = entry.getValue();

            Semester semester = semesterRepository.findByName(semesterKey)
                    .orElseThrow(() -> new NotFoundException("Semester " + semesterKey + " not found"));

            List<String> addedSubjects = new ArrayList<>();

            for (String subjectCode : subjectCodes) {
                Subject subject = subjectRepository.findBySubjectCode(subjectCode)
                        .orElseThrow(() -> new NotFoundException("Subject " + subjectCode + " not found"));

                if (addedSubjects.contains(subjectCode)) {
                    throw new DuplicateException("Subject '" + subjectCode + "' already exists in " + semesterKey);
                }
                addedSubjects.add(subjectCode);

                CourseSubject courseSubject = new CourseSubject();
                courseSubject.setCourse(course);
                courseSubject.setSubject(subject);
                courseSubject.setSemester(semester);

                courseSubjectsToSave.add(courseSubject);
            }
        }
    }


    private void saveCourseSubjects(CourseRequest request, Course course) {
        List<CourseSubject> courseSubjectsToSave = new ArrayList<>();
        try {
            processSemestersAndSubjects(request, course, courseSubjectsToSave, false);
            courseSubjectRepository.saveAll(courseSubjectsToSave);
        } catch (NotFoundException e) {
            courseRepository.delete(course);
            throw e;
        }
    }

    private CourseResponse convertToCourseResponse(Course course) {
        if (course == null) {
            return new CourseResponse(); // Return an empty object if needed
        }
        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(course.getId());

        Map<String, List<String>> semesterSubjects = courseSubjects.stream()
                .collect(Collectors.groupingBy(
                        cs -> cs.getSemester().getName(),
                        Collectors.mapping(cs -> cs.getSubject().getSubjectCode(), Collectors.toList())
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
