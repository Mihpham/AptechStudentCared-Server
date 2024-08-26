package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.StudentRequest;
import com.example.aptechstudentcaredserver.bean.response.StudentResponse;
import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.entity.*;
import com.example.aptechstudentcaredserver.enums.ClassMemberStatus;
import com.example.aptechstudentcaredserver.enums.Status;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.*;
import com.example.aptechstudentcaredserver.service.EmailGeneratorService;
import com.example.aptechstudentcaredserver.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final ClassRepository classRepository;
    private final UserCourseRepository userCourseRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ParentRepository parentRepository;
    private final GroupClassRepository groupClassRepository;
    private final EmailGeneratorService emailGeneratorService;

    @Override
    public void createStudent(StudentRequest studentRq) {
        // Check if class exists, create if not
        Class studentClass = classRepository.findByClassName(studentRq.getClassName());
        if (studentClass == null) {
            studentClass = new Class();
            studentClass.setClassName(studentRq.getClassName());
            studentClass.setCreatedAt(LocalDateTime.now());
            studentClass.setUpdatedAt(LocalDateTime.now());
            classRepository.save(studentClass);
        }

        // Check if course exists, create if not
        Course course = courseRepository.findByCourseName(studentRq.getCourse());
        if (course == null) {
            course = new Course();
            course.setCourseName(studentRq.getCourse());
            course.setCourseCode("C" + (courseRepository.count() + 1)); // Generate a simple course code
            course.setClassSchedule("TBD"); // You can adjust this
            course.setCreatedAt(LocalDateTime.now());
            course.setUpdatedAt(LocalDateTime.now());
            courseRepository.save(course);
        }

        // Check if role exists
        Role role = roleRepository.findByRoleName("Student");
        if (role == null) {
            role = new Role();
            role.setRoleName("Student");
            roleRepository.save(role);
        }

        String email = emailGeneratorService.generateUniqueEmail(studentRq.getFullName());


        // Create new User
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("@123456789"));
        user.setRole(role); // Set role as Role entity
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Save User
        userRepository.save(user);

        // Create new UserDetail
        UserDetail userDetail = new UserDetail();
        userDetail.setFullName(studentRq.getFullName());
        userDetail.setGender(studentRq.getGender());
        userDetail.setDob(studentRq.getDob());
        userDetail.setPhone(studentRq.getPhoneNumber());
        userDetail.setAddress(studentRq.getAddress());
        userDetail.setUser(user);

        // Check if parent exists
        Parent parent = new Parent();
        parent.setFullName(studentRq.getFullName());
        parent.setPhone(studentRq.getParentPhone());
        parent.setGender(studentRq.getParentGender());
        parent.setStudentRelation(studentRq.getStudentRelation());
        parent.setJob(studentRq.getParentJob());
        parent.setCreatedAt(LocalDateTime.now());
        parent.setUpdatedAt(LocalDateTime.now());
        if (parent != null) {
            parentRepository.save(parent); // Ensure parent is saved
            userDetail.setParent(parent);
        }

        // Save UserDetail
        userDetailRepository.save(userDetail);

        // Create UserCourse
        UserCourse userCourse = new UserCourse();
        userCourse.setUser(user);
        userCourse.setCourse(course);
        userCourse.setStartDate(LocalDateTime.now());
        userCourse.setEndDate(LocalDateTime.now().plusMonths(6));
        userCourse.setCreatedAt(LocalDateTime.now());
        userCourse.setUpdatedAt(LocalDateTime.now());

        // Save UserCourse
        userCourseRepository.save(userCourse);

        // Create GroupClass
        GroupClass groupClass = new GroupClass();
        groupClass.setUser(user);
        groupClass.setClasses(studentClass);
        groupClass.setJoinedDate(LocalDateTime.now());
        groupClass.setStatus(ClassMemberStatus.STUDYING);

        // Save GroupClass
        groupClassRepository.save(groupClass);
    }

    @Override
    public StudentResponse findStudentById(int studentId) {
        // Find the user by ID
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Get the user's class
        GroupClass groupClass = groupClassRepository.findByUserId(studentId)
                .orElseThrow(() -> new NotFoundException("Group class not found"));

        Class studentClass = groupClass.getClasses();

        // Get the user's courses
        List<UserCourse> userCourses = userCourseRepository.findByUserId(studentId);
        List<String> courses = userCourses.stream()
                .map(userCourse -> userCourse.getCourse().getCourseName())
                .collect(Collectors.toList());

        // Create StudentResponse DTO
        StudentResponse response = new StudentResponse();
        response.setUserId(user.getId());
        response.setFullName(user.getUserDetail().getFullName());
        response.setEmail(user.getEmail());
        response.setClassName(studentClass.getClassName());
        response.setCourses(courses);

        return response;
    }
}

