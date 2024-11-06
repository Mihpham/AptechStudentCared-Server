//package com.example.aptechstudentcaredserver.service;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import com.example.aptechstudentcaredserver.bean.request.CourseRequest;
//import com.example.aptechstudentcaredserver.bean.response.CourseResponse;
//import com.example.aptechstudentcaredserver.entity.Course;
//import com.example.aptechstudentcaredserver.enums.Status;
//import com.example.aptechstudentcaredserver.exception.DuplicateException;
//import com.example.aptechstudentcaredserver.exception.NotFoundException;
//import com.example.aptechstudentcaredserver.repository.CourseRepository;
//import com.example.aptechstudentcaredserver.repository.CourseSubjectRepository;
//import com.example.aptechstudentcaredserver.service.impl.CourseServiceImpl;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//public class CourseServiceTest {
//
//    @Mock
//    private CourseRepository courseRepository;
//
//    @Mock
//    private CourseSubjectRepository courseSubjectRepository;
//
//    @InjectMocks
//    private CourseServiceImpl courseService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void findAllCourses_shouldReturnListOfCourses() {
//        Course testCourse = new Course();
//        testCourse.setId(1);
//        testCourse.setCourseName("Math");
//        testCourse.setCourseCode("MATH101");
//        testCourse.setCourseCompTime("40 hours");
//        testCourse.setCreatedAt(LocalDateTime.now());
////        testCourse.set(Status.ACTIVE);
//
//        when(courseRepository.findAll()).thenReturn(Collections.singletonList(testCourse));
//
//        var result = courseService.getAllCourses();
//
//        assertEquals(1, result.size());
//        assertEquals("Math", result.get(0).getCourseName());
//    }
//
////    @Test
////    void findCourseById_shouldReturnCourseResponse_whenCourseExists() {
////        Course testCourse = new Course();
////        testCourse.setId(1);
////        testCourse.setCourseName("Math");
////        testCourse.setCourseCode("MATH101");
////        testCourse.setCourseCompTime("40 hours");
////
////        Map<String, List<String>> semesters = new HashMap<>();
////        semesters.put("Fall", List.of("Math101", "Science101"));
//////        testCourse.setSemesters(semesters);
////
////        when(courseRepository.findById(1)).thenReturn(Optional.of(testCourse));
////
////        CourseResponse result = courseService.getCourseById(1);
////
////        assertEquals("Math", result.getCourseName());
////        assertEquals("MATH101", result.getCourseCode());
////        assertEquals("40 hours", result.getCourseCompTime());
////        assertEquals(semesters, result.getSemesters());
////    }
//
//    @Test
//    void findCourseById_shouldThrowNotFoundException_whenCourseDoesNotExist() {
//        when(courseRepository.findById(1)).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundException.class, () -> courseService.getCourseById(1));
//    }
//
//    @Test
//    void addCourse_shouldThrowDuplicateException_whenCourseNameAlreadyExists() {
//        CourseRequest courseRequest = new CourseRequest();
//        courseRequest.setCourseName("Math");
//
//        when(courseRepository.findByCourseName("Math")).thenReturn(new Course());
//
//        assertThrows(DuplicateException.class, () -> courseService.createCourse(courseRequest));
//    }
//
////    @Test
////    void addCourse_shouldSaveNewCourse_whenCourseIsValid() {
////        CourseRequest courseRequest = new CourseRequest();
////        courseRequest.setCourseName("Math");
////        courseRequest.setCourseCode("MATH101");
////        courseRequest.setCourseCompTime("40 hours");
////
////        Map<String, List<String>> semesters = new HashMap<>();
////        semesters.put("Fall", List.of("Math101", "Science101"));
////        courseRequest.setSemesters(semesters);
////
////        when(courseRepository.findByCourseName("Math")).thenReturn(null);
////
////        courseService.createCourse(courseRequest);
////
////        verify(courseRepository, times(1)).save(any(Course.class));
////    }
//
//    @Test
//    void updateCourse_shouldThrowNotFoundException_whenCourseIdDoesNotExist() {
//        CourseRequest courseRequest = new CourseRequest();
//        courseRequest.setCourseName("Math");
//
//        when(courseRepository.findById(1)).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundException.class, () -> courseService.updateCourse(1, courseRequest));
//    }
//
////    @Test
////    void updateCourse_shouldUpdateCourse_whenCourseIdExists() {
////        Course testCourse = new Course();
////        testCourse.setId(1);
////        testCourse.setCourseName("Math");
////
////        CourseRequest courseRequest = new CourseRequest();
////        courseRequest.setCourseName("Science");
////        courseRequest.setCourseCode("SCI101");
////        courseRequest.setCourseCompTime("45 hours");
////
////        Map<String, List<String>> semesters = new HashMap<>();
////        semesters.put("Spring", List.of("Sci101", "Eng101"));
//////        courseRequest.setSemesters(semesters);
////
////        when(courseRepository.findById(1)).thenReturn(Optional.of(testCourse));
////        when(courseRepository.findByCourseName("Science")).thenReturn(null);
////
////        courseService.updateCourse(1, courseRequest);
////
////        verify(courseRepository, times(1)).save(testCourse);
////        assertEquals("Science", testCourse.getCourseName());
////        assertEquals("SCI101", testCourse.getCourseCode());
////        assertEquals("45 hours", testCourse.getCourseCompTime());
//////        assertEquals(semesters, testCourse.getSemesters());
////    }
//
//    @Test
//    void deleteCourse_shouldThrowNotFoundException_whenCourseDoesNotExist() {
//        when(courseRepository.findById(1)).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundException.class, () -> courseService.deleteCourse(1));
//    }
//
//    @Test
//    void deleteCourse_shouldDeleteCourse_whenCourseExists() {
//        Course testCourse = new Course();
//        testCourse.setId(1);
//
//        when(courseRepository.findById(1)).thenReturn(Optional.of(testCourse));
//
//        courseService.deleteCourse(1);
//
//        verify(courseRepository, times(1)).delete(testCourse);
//    }
//}
