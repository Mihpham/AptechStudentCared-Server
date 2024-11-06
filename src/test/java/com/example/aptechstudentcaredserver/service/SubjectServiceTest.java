//package com.example.aptechstudentcaredserver.service;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import com.example.aptechstudentcaredserver.bean.request.SubjectRequest;
//import com.example.aptechstudentcaredserver.bean.response.SubjectResponse;
//import com.example.aptechstudentcaredserver.entity.Subject;
//import com.example.aptechstudentcaredserver.entity.CourseSubject;
//import com.example.aptechstudentcaredserver.exception.DuplicateException;
//import com.example.aptechstudentcaredserver.exception.NotFoundException;
//import com.example.aptechstudentcaredserver.exception.EmptyListException;
//import com.example.aptechstudentcaredserver.repository.SubjectRepository;
//import com.example.aptechstudentcaredserver.repository.CourseSubjectRepository;
//import com.example.aptechstudentcaredserver.service.impl.SubjectServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//public class SubjectServiceTest {
//
//    @Mock
//    private SubjectRepository subjectRepository;
//
//    @Mock
//    private CourseSubjectRepository courseSubjectRepository;
//
//    @InjectMocks
//    private SubjectServiceImpl subjectService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void findAllSubject_shouldReturnListOfSubjects() {
//        Subject testSubject = new Subject();
//        testSubject.setId(1);
//        testSubject.setSubjectName("Mathematics");
//        testSubject.setSubjectCode("MATH101");
//        testSubject.setTotalHours(60);
//        testSubject.setCreatedAt(LocalDateTime.now());
//        testSubject.setUpdatedAt(LocalDateTime.now());
//
//        when(subjectRepository.findAll()).thenReturn(Collections.singletonList(testSubject));
//
//        List<SubjectResponse> result = subjectService.findAllSubject();
//
//        assertEquals(1, result.size());
//        assertEquals("Mathematics", result.get(0).getSubjectName());
//    }
//
//    @Test
//    void findAllSubject_shouldThrowEmptyListException_whenNoSubjectsFound() {
//        when(subjectRepository.findAll()).thenReturn(Collections.emptyList());
//
//        assertThrows(EmptyListException.class, () -> subjectService.findAllSubject());
//    }
//
//    @Test
//    void findSubjectById_shouldReturnSubjectResponse_whenSubjectExists() {
//        Subject testSubject = new Subject();
//        testSubject.setId(1);
//        testSubject.setSubjectName("Mathematics");
//        testSubject.setSubjectCode("MATH101");
//
//        when(subjectRepository.findById(1)).thenReturn(Optional.of(testSubject));
//
//        SubjectResponse result = subjectService.findSubjectById(1);
//
//        assertEquals("Mathematics", result.getSubjectName());
//    }
//
//    @Test
//    void findSubjectById_shouldThrowNotFoundException_whenSubjectDoesNotExist() {
//        when(subjectRepository.findById(1)).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundException.class, () -> subjectService.findSubjectById(1));
//    }
//
//    @Test
//    void createSubject_shouldThrowDuplicateException_whenSubjectNameExists() {
//        SubjectRequest subjectRequest = new SubjectRequest();
//        subjectRequest.setSubjectName("Mathematics");
//        subjectRequest.setSubjectCode("MATH101");
//        subjectRequest.setTotalHours(60);
//
//        when(subjectRepository.findBySubjectName("Mathematics")).thenReturn(Optional.of(new Subject()));
//
//        assertThrows(DuplicateException.class, () -> subjectService.createSubject(subjectRequest));
//    }
//
//    @Test
//    void createSubject_shouldThrowDuplicateException_whenSubjectCodeExists() {
//        SubjectRequest subjectRequest = new SubjectRequest();
//        subjectRequest.setSubjectName("Mathematics");
//        subjectRequest.setSubjectCode("MATH101");
//        subjectRequest.setTotalHours(60);
//
//        when(subjectRepository.findBySubjectName("Mathematics")).thenReturn(Optional.empty());
//        when(subjectRepository.findBySubjectCode("MATH101")).thenReturn(Optional.of(new Subject()));
//
//        assertThrows(DuplicateException.class, () -> subjectService.createSubject(subjectRequest));
//    }
//
//    @Test
//    void createSubject_shouldSaveNewSubject_whenSubjectIsValid() {
//        SubjectRequest subjectRequest = new SubjectRequest();
//        subjectRequest.setSubjectName("Mathematics");
//        subjectRequest.setSubjectCode("MATH101");
//        subjectRequest.setTotalHours(60);
//
//        when(subjectRepository.findBySubjectName("Mathematics")).thenReturn(Optional.empty());
//        when(subjectRepository.findBySubjectCode("MATH101")).thenReturn(Optional.empty());
//
//        subjectService.createSubject(subjectRequest);
//
//        verify(subjectRepository, times(1)).save(any(Subject.class));
//    }
//
//    @Test
//    void updateSubject_shouldThrowNotFoundException_whenSubjectIdDoesNotExist() {
//        SubjectRequest subjectRequest = new SubjectRequest();
//        subjectRequest.setSubjectName("Mathematics");
//        subjectRequest.setSubjectCode("MATH101");
//
//        when(subjectRepository.findById(1)).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundException.class, () -> subjectService.updateSubject(1, subjectRequest));
//    }
//
//    @Test
//    void updateSubject_shouldThrowDuplicateException_whenSubjectNameExists() {
//        Subject existingSubject = new Subject();
//        existingSubject.setId(1);
//        existingSubject.setSubjectName("Physics");
//
//        SubjectRequest subjectRequest = new SubjectRequest();
//        subjectRequest.setSubjectName("Mathematics");
//        subjectRequest.setSubjectCode("MATH101");
//
//        when(subjectRepository.findById(1)).thenReturn(Optional.of(existingSubject));
//        when(subjectRepository.findBySubjectName("Mathematics")).thenReturn(Optional.of(new Subject()));
//
//        assertThrows(DuplicateException.class, () -> subjectService.updateSubject(1, subjectRequest));
//    }
//
//    @Test
//    void updateSubject_shouldSaveUpdatedSubject_whenSubjectIdExists() {
//        Subject existingSubject = new Subject();
//        existingSubject.setId(1);
//        existingSubject.setSubjectName("Physics");
//
//        SubjectRequest subjectRequest = new SubjectRequest();
//        subjectRequest.setSubjectName("Mathematics");
//        subjectRequest.setSubjectCode("MATH101");
//        subjectRequest.setTotalHours(60);
//
//        when(subjectRepository.findById(1)).thenReturn(Optional.of(existingSubject));
//        when(subjectRepository.findBySubjectName("Mathematics")).thenReturn(Optional.empty());
//        when(subjectRepository.findBySubjectCode("MATH101")).thenReturn(Optional.empty());
//
//        subjectService.updateSubject(1, subjectRequest);
//
//        verify(subjectRepository, times(1)).save(existingSubject);
//        assertEquals("Mathematics", existingSubject.getSubjectName());
//    }
//
//    @Test
//    void deleteSubject_shouldThrowNotFoundException_whenSubjectDoesNotExist() {
//        when(subjectRepository.existsById(1)).thenReturn(false);
//
//        assertThrows(NotFoundException.class, () -> subjectService.deleteSubject(1));
//    }
//
//    @Test
//    void deleteSubject_shouldDeleteSubject_whenSubjectExists() {
//        Subject subject = new Subject();
//        subject.setId(1);
//
//        // Mocking findBySubjectId to return a non-empty list
//        when(courseSubjectRepository.findBySubjectId(1)).thenReturn(Collections.singletonList(new CourseSubject()));
//
//        // Mocking existsById to return true
//        when(subjectRepository.existsById(1)).thenReturn(true);
//
//        // Call the deleteSubject method
//        subjectService.deleteSubject(1);
//
//        // Verify deleteAll is called
//        verify(courseSubjectRepository, times(1)).deleteAll(anyList());
//
//        // Verify deleteById is called
//        verify(subjectRepository, times(1)).deleteById(1);
//    }
//
//}
//
