package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.StudentRequest;
import com.example.aptechstudentcaredserver.bean.response.StudentResponse;
import com.example.aptechstudentcaredserver.entity.*;
import com.example.aptechstudentcaredserver.enums.Status;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.*;
import com.example.aptechstudentcaredserver.service.EmailGeneratorService;
import com.example.aptechstudentcaredserver.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDetailRepository userDetailRepository;
    @Mock
    private ClassRepository classRepository;
    @Mock
    private UserCourseRepository userCourseRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ParentRepository parentRepository;
    @Mock
    private GroupClassRepository groupClassRepository;
    @Mock
    private EmailGeneratorService emailGeneratorService;

    @InjectMocks
    private StudentServiceImpl studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    void testFindAllStudent() {
//        User user = new User();
//        user.setId(1);
//        user.setEmail("student@example.com");
//
//        when(userRepository.findByRoleRoleName("STUDENT")).thenReturn(List.of(user));
////        when(groupClassRepository.findByUserId(user.getId())).thenReturn(new GroupClass());
//
//        List<StudentResponse> students = studentService.findAllStudent();
//
//        assertNotNull(students);
//        assertEquals(1, students.size());
//        assertEquals(user.getEmail(), students.get(0).getEmail());
//        verify(userRepository, times(1)).findByRoleRoleName("STUDENT");
//    }

//    @Test
//    void testFindStudentById_Success() {
//        int studentId = 1;
//        User user = new User();
//        user.setId(studentId);
//        user.setEmail("student@example.com");
//        GroupClass groupClass = new GroupClass();
//
//        when(userRepository.findById(studentId)).thenReturn(Optional.of(user));
////        when(groupClassRepository.findByUserId(studentId)).thenReturn(groupClass);
//
//        StudentResponse studentResponse = studentService.findStudentById(studentId);
//
//        assertNotNull(studentResponse);
//        assertEquals(user.getEmail(), studentResponse.getEmail());
//        verify(userRepository, times(1)).findById(studentId);
//    }

    @Test
    void testFindStudentById_NotFound() {
        int studentId = 1;

        when(userRepository.findById(studentId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            studentService.findStudentById(studentId);
        });

        assertEquals("User not found with id " + studentId, thrown.getMessage());
        verify(userRepository, times(1)).findById(studentId);
    }

//    @Test
//    void testCreateStudent() {
//        StudentRequest studentRequest = new StudentRequest();
//        studentRequest.setFullName("John Doe");
//        studentRequest.setClassName("Class A");
//        studentRequest.setCourses(new HashSet<>(List.of("Math", "English")));
//
//
//        Role role = new Role();
//        role.setRoleName("STUDENT");
//
//        when(roleRepository.findByRoleName("STUDENT")).thenReturn(role);
//        when(emailGeneratorService.generateUniqueEmail("John Doe")).thenReturn("john.doe@example.com");
//
//        studentService.createStudent(studentRequest);
//
//        verify(userRepository, times(1)).save(any(User.class));
//        verify(userDetailRepository, times(1)).save(any(UserDetail.class));
//        verify(parentRepository, times(1)).save(any(Parent.class));
//        verify(userCourseRepository, times(2)).save(any(UserCourse.class));
//        verify(groupClassRepository, times(1)).save(any(GroupClass.class));
//    }

//    @Test
//    void testUpdateStudent_Success() {
//        int studentId = 1;
//        StudentRequest studentRequest = new StudentRequest();
//        studentRequest.setFullName("John Smith");
//        // Thiết lập các thuộc tính khác nếu cần
//
//        User mockUser = new User();
//        UserDetail mockUserDetail = new UserDetail();
//        mockUser.setUserDetail(mockUserDetail);
//
//        when(userRepository.findById(studentId)).thenReturn(Optional.of(mockUser));
//        when(groupClassRepository.findByUserId(studentId)).thenReturn(Optional.of(new GroupClass()));
//
//        // Gọi phương thức cần test
//        StudentResponse response = studentService.updateStudent(studentId, studentRequest);
//
//        // Kiểm tra kết quả
//        assertNotNull(response);
//        // Thực hiện các kiểm tra khác cần thiết
//    }


    @Test
    void testUpdateStudent_NotFound() {
        int studentId = 1;
        StudentRequest studentRequest = new StudentRequest();
        studentRequest.setFullName("John Smith");
        when(userRepository.findById(studentId)).thenReturn(Optional.empty());
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            studentService.updateStudent(studentId, studentRequest);
        });

        assertTrue(thrown.getMessage().contains("User not found with ID: " + studentId));

        verify(userRepository, times(1)).findById(studentId);
    }

//    @Test
//    void testDeleteStudent_Success() {
//        int studentId = 1;
//
//        // Giả lập findUserById trả về một User hợp lệ
//        User user = new User();
//        when(userRepository.findById(studentId)).thenReturn(Optional.of(user));
//
//        // Giả lập findGroupClassByUserId trả về một GroupClass hợp lệ
//        GroupClass groupClass = new GroupClass();
//        when(groupClassRepository.findByUserId(studentId)).thenReturn(Optional.of(groupClass));
//
//        // Thực hiện phương thức xóa
//        studentService.deleteStudent(studentId);
//
//        // Xác nhận các phương thức đã được gọi
//        verify(userRepository, times(1)).findById(studentId);
//        verify(groupClassRepository, times(1)).findByUserId(studentId);
//        verify(userRepository, times(1)).delete(user);
//        verify(groupClassRepository, times(1)).delete(groupClass);
//    }


    @Test
    void testDeleteStudent_NotFound() {
        int studentId = 1;

        when(userRepository.findById(studentId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            studentService.deleteStudent(studentId);
        });

        assertEquals("Student not found with id " + studentId, thrown.getMessage());
        verify(userRepository, times(1)).findById(studentId);
    }
}
