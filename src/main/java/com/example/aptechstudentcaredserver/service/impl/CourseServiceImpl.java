package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.bean.request.CourseRequest;
import com.example.aptechstudentcaredserver.bean.response.CourseResponse;
import com.example.aptechstudentcaredserver.entity.Course;
import com.example.aptechstudentcaredserver.entity.CourseSubject;
import com.example.aptechstudentcaredserver.entity.Semester;
import com.example.aptechstudentcaredserver.entity.Subject;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.exception.EmptyListException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.*;
import com.example.aptechstudentcaredserver.service.CourseService;
import com.example.aptechstudentcaredserver.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final ClassRepository classRepository;

    @Override
    public List<CourseResponse> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        if(courses.isEmpty()){
            throw new EmptyListException("courses not found");
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
        String generatedCourseCode = generateCourseCode(request.getCourseName());
        Course existCourseByName = courseRepository.findByCourseName(request.getCourseName());
        if (existCourseByName != null) {
            throw new DuplicateException("Course with name '" + request.getCourseName() + "' already exists.");
        }
        Course existCourseByCode = courseRepository.findByCourseCode(generatedCourseCode);
        if (existCourseByCode != null) {
            throw new DuplicateException("Course with code '" + generatedCourseCode + "' already exists.");
        }

        if (request.getSemesters() == null || request.getSemesters().isEmpty()) {
            throw new NotFoundException("At least one semester and subject must be provided");
        }
        Course course = new Course();
        course.setCourseName(request.getCourseName());
        course.setCourseCode(generatedCourseCode); // Sử dụng mã khóa học tự động tạo
        course.setCourseCompTime(request.getCourseCompTime());
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());

        course = courseRepository.save(course);

        List<CourseSubject> courseSubjectsToSave = new ArrayList<>();

        try {
            processSemestersAndSubjects(request, course, courseSubjectsToSave);
            courseSubjectRepository.saveAll(courseSubjectsToSave);
        } catch (NotFoundException e) {
            courseRepository.delete(course);
            throw e;
        }
    }

    @Override
    public CourseResponse updateCourse(int courseId, CourseRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course with ID " + courseId + " not found"));

        Course existCourseByName = courseRepository.findByCourseName(request.getCourseName());
        if (existCourseByName != null && existCourseByName.getId() != courseId) {
            throw new DuplicateException("Course with name '" + request.getCourseName() + "' already exists.");
        }

        String generatedCourseCode = generateCourseCode(request.getCourseName());

        Course existCourseByCode = courseRepository.findByCourseCode(generatedCourseCode);
        if (existCourseByCode != null && existCourseByCode.getId() != courseId) {
            throw new DuplicateException("Course with code '" + generatedCourseCode + "' already exists.");
        }

        course.setCourseName(request.getCourseName());
        course.setCourseCode(generatedCourseCode);
        course.setCourseCompTime(request.getCourseCompTime());
        course.setUpdatedAt(LocalDateTime.now());

        courseRepository.save(course);

        List<CourseSubject> courseSubjectsToSave = new ArrayList<>();
        List<CourseSubject> courseSubjectsToDelete = courseSubjectRepository.findByCourseId(courseId);

        List<CourseSubject> subjectsToDelete = courseSubjectsToDelete.stream()
                .filter(cs -> !request.getSemesters().containsKey(cs.getSemester().getName()) ||
                        !request.getSemesters().get(cs.getSemester().getName()).contains(cs.getSubject().getSubjectCode()))
                .collect(Collectors.toList());

        courseSubjectRepository.deleteAll(subjectsToDelete);

        try {
            processSemestersAndSubjects(request, course, courseSubjectsToSave);
            courseSubjectRepository.saveAll(courseSubjectsToSave);
        } catch (NotFoundException e) {
            throw e;
        }
        return convertToCourseResponse(course);
    }


    @Override
    public void deleteCourse(int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course with ID " + courseId + " not found"));

        List<Class> classes = classRepository.findByCourseId(courseId);

        try {

            if (!classes.isEmpty()) {
                for (Class newClass : classes) {
                    newClass.setCourse(null);
                    classRepository.save(newClass);
                }
            }

            List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(courseId);
            if (!courseSubjects.isEmpty()) {
                courseSubjectRepository.deleteAll(courseSubjects);
            }

            courseRepository.delete(course);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete course with ID " + courseId, e);
        }
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
        if (course == null) {
            return new CourseResponse(); // return object empty if need
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

    public String generateCourseCode(String subjectName) {
        List<String> ignoreWords = Arrays.asList("in", "on", "at", "of", "the", "and", "a", "an", "to", "for");

        String[] words = subjectName.trim().split("\\s+");
        if (words.length == 1) {
            return words[0].toUpperCase().substring(0, Math.min(words[0].length(), 6));
        }
        StringBuilder code = new StringBuilder();
        for (String word : words) {
            if (!ignoreWords.contains(word.toLowerCase())) {
                if (code.length() < 6) {
                    code.append(Character.toUpperCase(word.charAt(0)));
                } else {
                    break; // Đảm bảo mã không quá 6 ký tự
                }
            }
        }

        return code.toString();
    }


}
