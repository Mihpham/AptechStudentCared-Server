//package com.example.aptechstudentcaredserver.service;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
//import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
//import com.example.aptechstudentcaredserver.entity.Class;
//import com.example.aptechstudentcaredserver.enums.DayOfWeeks;
//import com.example.aptechstudentcaredserver.enums.Status;
//import com.example.aptechstudentcaredserver.exception.DuplicateException;
//import com.example.aptechstudentcaredserver.exception.NotFoundException;
//import com.example.aptechstudentcaredserver.repository.ClassRepository;
//import com.example.aptechstudentcaredserver.repository.GroupClassRepository;
//import com.example.aptechstudentcaredserver.repository.UserCourseRepository;
//import com.example.aptechstudentcaredserver.service.impl.ClassServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Optional;
//
//public class ClassServiceTest {
//
//    @Mock
//    private ClassRepository classRepository;
//
//    @Mock
//    private GroupClassRepository groupClassRepository;
//
//    @Mock
//    private UserCourseRepository userCourseRepository;
//
//    @InjectMocks
//    private ClassServiceImpl classService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void findAllClass_shouldReturnListOfClasses() {
//        Class testClass = new Class();
//        testClass.setId(1);
//        testClass.setClassName("Math");
//        testClass.setCenter("Center A");
//        testClass.setStartHour(LocalTime.of(8, 30)); ;  // 8:30 AM
//        testClass.setEndHour(LocalTime.of(10, 0)); ;    // 10:00 AM
//        testClass.setDays(Arrays.asList(DayOfWeeks.MONDAY, DayOfWeeks.WEDNESDAY));
//        testClass.setCreatedAt(LocalDateTime.now());
//        testClass.setStatus(Status.STUDYING);
//
//        when(classRepository.findAll()).thenReturn(Collections.singletonList(testClass));
//
//        var result = classService.findAllClass();
//
//        assertEquals(1, result.size());
//        assertEquals("Math", result.get(0).getClassName());
//    }
//
//    @Test
//    void findClassById_shouldReturnClassResponse_whenClassExists() {
//        Class testClass = new Class();
//        testClass.setId(1);
//        testClass.setClassName("Math");
//
//        when(classRepository.findById(1)).thenReturn(Optional.of(testClass));
//
//        var result = classService.findClassById(1);
//
//        assertEquals("Math", result.getClassName());
//    }
//
//    @Test
//    void findClassById_shouldThrowNotFoundException_whenClassDoesNotExist() {
//        when(classRepository.findById(1)).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundException.class, () -> classService.findClassById(1));
//    }
//
//    @Test
//    void addClass_shouldThrowDuplicateException_whenClassNameAlreadyExists() {
//        ClassRequest classRequest = new ClassRequest();
//        classRequest.setClassName("Math");
//
//        when(classRepository.findByClassName("Math")).thenReturn(new Class());
//
//        assertThrows(DuplicateException.class, () -> classService.addClass(classRequest));
//    }
//
//    @Test
//    void addClass_shouldSaveNewClass_whenClassIsValid() {
//        ClassRequest classRequest = new ClassRequest();
//        classRequest.setClassName("Math");
//        classRequest.setCenter("Center A");
//        classRequest.setStartHour(LocalTime.of(8, 30)); ;  // 8:30 AM
//        classRequest.setEndHour(LocalTime.of(10, 0)); ;    // 10:00 AM
//        classRequest.setDays(Arrays.asList(DayOfWeeks.MONDAY, DayOfWeeks.WEDNESDAY));
//        classRequest.setStatus("Active");
//
//        when(classRepository.findByClassName("Math")).thenReturn(null);
//
//        classService.addClass(classRequest);
//
//        verify(classRepository, times(1)).save(any(Class.class));
//    }
//
//    @Test
//    void updateClass_shouldThrowNotFoundException_whenClassIdDoesNotExist() {
//        ClassRequest classRequest = new ClassRequest();
//        classRequest.setClassName("Math");
//
//        when(classRepository.findById(1)).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundException.class, () -> classService.updateClass(1, classRequest));
//    }
//
//    @Test
//    void updateClass_shouldUpdateClass_whenClassIdExists() {
//        Class testClass = new Class();
//        testClass.setId(1);
//        testClass.setClassName("Math");
//
//        ClassRequest classRequest = new ClassRequest();
//        classRequest.setClassName("Science");
//        classRequest.setCenter("Center B");
//        classRequest.setStartHour(LocalTime.of(8, 30)); ;  // 8:30 AM
//        classRequest.setEndHour(LocalTime.of(10, 0)); ;    // 10:00 AM
//        classRequest.setDays(Arrays.asList(DayOfWeeks.MONDAY, DayOfWeeks.WEDNESDAY));
//        classRequest.setStatus("ACTIVE");
//
//        when(classRepository.findById(1)).thenReturn(Optional.of(testClass));
//        when(classRepository.findByClassName("Science")).thenReturn(null);
//
//        classService.updateClass(1, classRequest);
//
//        verify(classRepository, times(1)).save(testClass);
//        assertEquals("Science", testClass.getClassName());
//    }
//
//    @Test
//    void deleteClass_shouldThrowNotFoundException_whenClassDoesNotExist() {
//        when(classRepository.findById(1)).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundException.class, () -> classService.deleteClass(1));
//    }
//
//    @Test
//    void deleteClass_shouldDeleteClass_whenClassExists() {
//        Class testClass = new Class();
//        testClass.setId(1);
//
//        when(classRepository.findById(1)).thenReturn(Optional.of(testClass));
//
//        classService.deleteClass(1);
//
//        verify(classRepository, times(1)).delete(testClass);
//    }
//}
//
