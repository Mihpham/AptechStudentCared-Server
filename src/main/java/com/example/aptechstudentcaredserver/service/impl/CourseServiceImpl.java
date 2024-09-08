package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.CourseRequest;
import com.example.aptechstudentcaredserver.bean.response.CourseResponse;
import com.example.aptechstudentcaredserver.bean.response.SubjectResponse;
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
        Course existCourse = courseRepository.findByCourseName(request.getCourseName());

        if (existCourse != null) {
            throw new DuplicateException("Course with this name already exists");
        }

        Course course = new Course();

        course.setCourseName(request.getCourseName());
        course.setCourseCode(request.getCourseCode());
        course.setClassSchedule(request.getClassSchedule());
        course.setCourseCompTime(request.getCourseCompTime());
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());

        course = courseRepository.save(course);
        processSemestersAndSubjects(request, course);
    }

    @Override
    public CourseResponse updateCourse(int courseId, CourseRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course with id " + courseId + " not found"));

        course.setCourseName(request.getCourseName());
        course.setCourseCode(request.getCourseCode());
        course.setClassSchedule(request.getClassSchedule());
        course.setCourseCompTime(request.getCourseCompTime());
        course.setUpdatedAt(LocalDateTime.now());

        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(courseId);
        if (!courseSubjects.isEmpty()) {
            courseSubjectRepository.deleteAll(courseSubjects);
        }

        courseRepository.save(course);

        processSemestersAndSubjects(request, course);

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
