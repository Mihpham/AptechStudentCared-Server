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
    public List<StudentResponse> findAllStudent() {
        // Retrieve all users with the role "Student"
        List<User> users = userRepository.findByRoleRoleName("Student");

        // Map the list of User entities to a list of StudentResponse DTOs
        return users.stream().map(user -> {
            // Find the group class for the user
            GroupClass groupClass = groupClassRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new NotFoundException("Group class not found for user id " + user.getId()));

            // Find the class associated with the group class
            Class studentClass = groupClass.getClasses();

            // Find the courses associated with the user
            List<UserCourse> userCourses = userCourseRepository.findByUserId(user.getId());
            List<String> courses = userCourses.stream()
                    .map(userCourse -> userCourse.getCourse().getCourseName())
                    .collect(Collectors.toList());

            // Create and return the StudentResponse DTO
            StudentResponse response = new StudentResponse();
            response.setUserId(user.getId());
            response.setFullName(user.getUserDetail().getFullName());
            response.setEmail(user.getEmail());
            response.setClassName(studentClass.getClassName());
            response.setCourses(courses);
            response.setRollNumber(user.getUserDetail().getRollNumber()); // Set roll number
            response.setPhone(user.getUserDetail().getPhone()); // Set phone
            response.setStatus(groupClass.getStatus().name()); // Use status from GroupClass

            return response;
        }).collect(Collectors.toList());
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
        response.setRollNumber(user.getUserDetail().getRollNumber()); // Set roll number
        response.setPhone(user.getUserDetail().getPhone()); // Set phone
        response.setStatus(user.getStatus().name()); // Convert enum to String

        return response;
    }

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
        userDetail.setRollNumber(studentRq.getRollNumber());
        userDetail.setFullName(studentRq.getFullName());
        userDetail.setGender(studentRq.getGender());
        userDetail.setDob(studentRq.getDob());
        userDetail.setPhone(studentRq.getPhoneNumber());
        userDetail.setAddress(studentRq.getAddress());
        userDetail.setUser(user);

        // Check if parent exists
        Parent parent = new Parent();
        parent.setFullName(studentRq.getParentFullName()); // Updated field name
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

        // Create and save UserCourses
        if (studentRq.getCourses() != null) {
            for (String courseName : studentRq.getCourses()) {
                Course course = courseRepository.findByCourseName(courseName.trim());
                if (course == null) {
                    course = new Course();
                    course.setCourseName(courseName.trim());
                    course.setCourseCode("C" + (courseRepository.count() + 1)); // Generate a simple course code
                    course.setClassSchedule("TBD"); // You can adjust this
                    course.setCreatedAt(LocalDateTime.now());
                    course.setUpdatedAt(LocalDateTime.now());
                    courseRepository.save(course);
                }

                UserCourse userCourse = new UserCourse();
                userCourse.setUser(user);
                userCourse.setCourse(course);
                userCourse.setStartDate(LocalDateTime.now());
                userCourse.setEndDate(LocalDateTime.now().plusMonths(6));
                userCourse.setCreatedAt(LocalDateTime.now());
                userCourse.setUpdatedAt(LocalDateTime.now());

                userCourseRepository.save(userCourse);
            }
        }

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
    public StudentResponse updateStudent(int studentId, StudentRequest studentRq) {
        // Find the user by ID
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("User not found with id " + studentId));

        // Retrieve UserDetail and other associated entities
        UserDetail userDetail = user.getUserDetail();

        // Update user details
        if (studentRq.getFullName() != null) {
            userDetail.setFullName(studentRq.getFullName());
        }
        if (studentRq.getEmail() != null) {
            user.setEmail(studentRq.getEmail());
        }
        if (studentRq.getRollNumber() != null) {
            userDetail.setRollNumber(studentRq.getRollNumber());
        }
        if (studentRq.getPhoneNumber() != null) {
            userDetail.setPhone(studentRq.getPhoneNumber());
        }

        // Update the class if needed
        if (studentRq.getClassName() != null) {
            Class studentClass = classRepository.findByClassName(studentRq.getClassName());
            if (studentClass == null) {
                studentClass = new Class();
                studentClass.setClassName(studentRq.getClassName());
                studentClass.setCreatedAt(LocalDateTime.now());
                studentClass.setUpdatedAt(LocalDateTime.now());
                classRepository.save(studentClass);
            }

            // Update GroupClass with the new class
            GroupClass groupClass = groupClassRepository.findByUserId(studentId)
                    .orElseThrow(() -> new NotFoundException("Group class not found for user id " + studentId));
            groupClass.setClasses(studentClass);
            groupClassRepository.save(groupClass);
        }

        // Update courses
        if (studentRq.getCourses() != null) {
            // Remove existing courses
            List<UserCourse> userCourses = userCourseRepository.findByUserId(studentId);
            if (!userCourses.isEmpty()) {
                userCourseRepository.deleteAll(userCourses);
            }

            // Add new courses
            for (String courseName : studentRq.getCourses()) {
                Course course = courseRepository.findByCourseName(courseName.trim());
                if (course == null) {
                    course = new Course();
                    course.setCourseName(courseName.trim());
                    course.setCourseCode("C" + (courseRepository.count() + 1)); // Generate a simple course code
                    course.setClassSchedule("TBD"); // You can adjust this
                    course.setCreatedAt(LocalDateTime.now());
                    course.setUpdatedAt(LocalDateTime.now());
                    courseRepository.save(course);
                }

                UserCourse userCourse = new UserCourse();
                userCourse.setUser(user);
                userCourse.setCourse(course);
                userCourse.setStartDate(LocalDateTime.now());
                userCourse.setEndDate(LocalDateTime.now().plusMonths(6));
                userCourse.setCreatedAt(LocalDateTime.now());
                userCourse.setUpdatedAt(LocalDateTime.now());

                userCourseRepository.save(userCourse);
            }
        }

        // Update GroupClass status
        if (studentRq.getStatus() != null) {
            GroupClass groupClass = groupClassRepository.findByUserId(studentId)
                    .orElseThrow(() -> new NotFoundException("Group class not found for user id " + studentId));
            groupClass.setStatus(ClassMemberStatus.valueOf(studentRq.getStatus()));
            groupClassRepository.save(groupClass);
        }

        // Save updated User and UserDetail
        userRepository.save(user);
        userDetailRepository.save(userDetail);

        // Prepare StudentResponse DTO
        GroupClass groupClass = groupClassRepository.findByUserId(studentId)
                .orElseThrow(() -> new NotFoundException("Group class not found for user id " + studentId));
        StudentResponse response = new StudentResponse();
        response.setUserId(user.getId());
        response.setFullName(userDetail.getFullName());
        response.setEmail(user.getEmail());
        response.setClassName(groupClass.getClasses().getClassName());
        response.setCourses(userCourseRepository.findByUserId(studentId).stream()
                .map(userCourse -> userCourse.getCourse().getCourseName())
                .collect(Collectors.toList()));
        response.setRollNumber(userDetail.getRollNumber());
        response.setPhone(userDetail.getPhone());
        response.setStatus(groupClass.getStatus().name());

        return response;
    }






    @Override
    public void deleteStudent(int studentId) {
        // Ensure the student exists before attempting to delete
        if (!userRepository.existsById(studentId)) {
            throw new NotFoundException("Student not found with id " + studentId);
        }
        // Delete the student from the repository
        userRepository.deleteById(studentId);
    }

}

