package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.CourseRequest;
import com.example.aptechstudentcaredserver.bean.request.SubjectRequest;
import com.example.aptechstudentcaredserver.bean.response.CourseResponse;
import com.example.aptechstudentcaredserver.bean.response.SubjectResponse;
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
import com.example.aptechstudentcaredserver.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    private final CourseSubjectRepository courseSubjectRepository;
    private final SemesterService semesterService;
    private final SubjectService subjectService;

    @Override
    public List<CourseResponse> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
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
        // Kiểm tra xem khóa học đã tồn tại hay chưa
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

        // Tạo mới đối tượng Course
        Course course = new Course();
        course.setCourseName(request.getCourseName());
        course.setCourseCode(request.getCourseCode());
        course.setCourseCompTime(request.getCourseCompTime());
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());

        // Lưu khóa học vào cơ sở dữ liệu
        course = courseRepository.save(course);


        // Lưu thông tin các môn học cho khóa học
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
        String[] projectNames = {
                "Project Semester 1",
                "Project Semester 2",
                "Project Semester 3",
                "Project Semester 4"
        };

        String[] projectCodes = {
                "PROJECT_SEM1",
                "PROJECT_SEM2",
                "PROJECT_SEM3",
                "PROJECT_SEM4"
        };

        for (int i = 0; i < projectNames.length; i++) {
            Optional<Subject> projectSubjectOpt = subjectRepository.findBySubjectCode(projectCodes[i]);

            if (projectSubjectOpt.isEmpty()) {
                SubjectRequest projectRequest = new SubjectRequest();
                projectRequest.setSubjectName(projectNames[i]); // Tên môn học theo kỳ
                projectRequest.setSubjectCode(projectCodes[i]); // Mã môn học theo kỳ
                projectRequest.setTotalHours(60); // Tổng số giờ học cho môn này
                subjectService.createSubject(projectRequest);
                System.out.println("Created new subject: " + projectRequest.getSubjectName());
            } else {
                System.out.println("Subject already exists: " + projectSubjectOpt.get().getSubjectName());
            }
        }

        request.getSemesters().forEach((semesterName, subjects) -> {
            boolean hasProjectSubject = subjects.stream()
                    .anyMatch(subject -> subject.equalsIgnoreCase("PROJECT_" + semesterName));
            if (!hasProjectSubject) {
                subjects.add("PROJECT_" + semesterName);
            }
        });
        List<CourseSubject> courseSubjectsToSave = new ArrayList<>();
        try {
            processSemestersAndSubjects(request, course, courseSubjectsToSave, false);
            courseSubjectRepository.saveAll(courseSubjectsToSave);
        } catch (NotFoundException e) {
            courseRepository.delete(course);
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
