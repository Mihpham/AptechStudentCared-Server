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
    public void createCourse(CourseRequest courseRequest) {
        try {
            if (courseRequest.getCourseName() == null || courseRequest.getCourseName().isEmpty()) {
                throw new IllegalArgumentException("Subject name cannot be null or empty.");
            }

            Course course = new Course();
            course.setCourseName(courseRequest.getCourseName());
            course.setCourseCode(courseRequest.getCourseCode());
            course.setCourseCompTime(courseRequest.getCourseCompTime());
            course.setCreatedAt(LocalDateTime.now());
            course.setUpdatedAt(LocalDateTime.now());

            addSubjectsToCourse(course, courseRequest.getSubjectsPerSemester());

            courseRepository.save(course);
        } catch (
                DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Course with the same name already exists.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create subject.", e);
        }
    }

    private void addSubjectsToCourse(Course course, Map<Integer, List<Integer>> subjectsPerSemester) {
        if (subjectsPerSemester != null) {
            for (Map.Entry<Integer, List<Integer>> entry : subjectsPerSemester.entrySet()) {
                Integer semesterId = entry.getKey();
                List<Integer> subjectIds = entry.getValue();

                Semester semester = semesterRepository.findById(semesterId)
                        .orElseThrow(() -> new RuntimeException("Semester not found"));

                for (Integer subjectId : subjectIds) {
                    Subject subject = subjectRepository.findById(subjectId)
                            .orElseThrow(() -> new RuntimeException("Subject not found"));

                    CourseSubject courseSubject = new CourseSubject();
                    courseSubject.setCourse(course);
                    courseSubject.setSemester(semester);
                    courseSubject.setSubject(subject);

                    course.getCourseSubjects().add(courseSubject);
                }
            }
        }
    }

    @Override
    public CourseResponse updateCourse(int courseId, CourseRequest courseRequest) {
        try {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new NotFoundException("Course with " + courseId + " not found"));

            course.setCourseName(courseRequest.getCourseName());
            course.setCourseCode(courseRequest.getCourseCode());
            course.setCourseCompTime(courseRequest.getCourseCompTime());
            course.setUpdatedAt(LocalDateTime.now());

            // Clear old course-subject relations
            course.getCourseSubjects().clear();
            courseSubjectRepository.deleteById(courseId);

            // Add new subjects per semester
            addSubjectsToCourse(course, courseRequest.getSubjectsPerSemester());

            Course updatedCourse = courseRepository.save(course);
            return convertToCourseResponse(updatedCourse);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update course.", e);
        }

    }

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
