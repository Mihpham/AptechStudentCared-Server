package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.TeacherRequest;
import com.example.aptechstudentcaredserver.bean.response.TeacherResponse;
import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.entity.Role;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.entity.UserDetail;
import com.example.aptechstudentcaredserver.entity.UserSubject;
import com.example.aptechstudentcaredserver.enums.Status;
import com.example.aptechstudentcaredserver.repository.*;
import com.example.aptechstudentcaredserver.service.EmailGeneratorService;
import com.example.aptechstudentcaredserver.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailGeneratorService emailGeneratorService;
    private final RoleRepository roleRepository;
    private final ClassRepository classRepository;
    private final UserSubjectRepository userSubjectRepository;

    @Override
    public List<TeacherResponse> findAllTeachers() {
        Role teacherRole = roleRepository.findByRoleName("TEACHER");
        if (teacherRole == null) {
            return Collections.emptyList();
        }

        List<User> teachers = userRepository.findByRole(teacherRole);
        return teachers.stream()
                .map(this::convertToTeacherResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TeacherResponse findTeacherById(int teacherId) {
        User user = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + teacherId));

        if (!"TEACHER".equals(user.getRole().getRoleName())) {
            throw new RuntimeException("User with id " + teacherId + " is not a teacher.");
        }

        return convertToTeacherResponse(user);
    }


    @Override
    public TeacherResponse updateTeacher(TeacherRequest teacherRequest, int teacherId) {
        User user = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + teacherId));

        if (!"TEACHER".equals(user.getRole().getRoleName())) {
            throw new RuntimeException("User with id " + teacherId + " is not a teacher.");
        }

        UserDetail userDetail = user.getUserDetail();
        String oldFullName = userDetail.getFullName();

        updateTeacherDetail(user, userDetail, teacherRequest);

        if (teacherRequest.getFullName() != null && !teacherRequest.getFullName().equals(oldFullName)) {
            String newEmail = emailGeneratorService.generateUniqueEmail(teacherRequest.getFullName());
            user.setEmail(newEmail);
        }

        userRepository.save(user);
        userDetailRepository.save(userDetail);

        return convertToTeacherResponse(user);
    }

    @Override
    public void deleteTeacher(int teacherId) {
        User user = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + teacherId));

        if (!"TEACHER".equals(user.getRole().getRoleName())) {
            throw new RuntimeException("User with id " + teacherId + " is not a teacher.");
        }

        userRepository.delete(user);
        if (user.getUserDetail() != null) {
            userDetailRepository.delete(user.getUserDetail());
        }
    }

    @Override
    public void registerTeacher(TeacherRequest teacherRequest) {
        Role role = findOrCreateRole("TEACHER");
        String email = emailGeneratorService.generateUniqueEmail(teacherRequest.getFullName());
        User user = createUser(teacherRequest, role, email);
        userRepository.save(user);
        UserDetail userDetail = createUserDetail(teacherRequest, user);
        userDetailRepository.save(userDetail);
    }


    private Role findOrCreateRole(String roleName) {
        return Optional.ofNullable(roleRepository.findByRoleName(roleName))
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleName(roleName);
                    return roleRepository.save(role);
                });
    }

    private User createUser(TeacherRequest teacherRq, Role role, String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("@123456789"));
        user.setRole(role);
        user.setStatus(Status.TEACHING);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private UserDetail createUserDetail(TeacherRequest teacherRq, User user) {
        UserDetail userDetail = new UserDetail();
        userDetail.setFullName(teacherRq.getFullName());
        userDetail.setPhone(teacherRq.getPhoneNumber());
        userDetail.setDob(teacherRq.getDob());
        userDetail.setAddress(teacherRq.getAddress());
        userDetail.setGender(teacherRq.getGender());
        userDetail.setImage(null);
        userDetail.setParent(null);
        userDetail.setUser(user);
        userDetailRepository.save(userDetail);
        return userDetail;
    }

    private void updateTeacherDetail(User user, UserDetail userDetail, TeacherRequest teacherRq) {
        if (teacherRq.getFullName() != null) {
            userDetail.setFullName(teacherRq.getFullName());
        }
        if (teacherRq.getImage() != null) {
            userDetail.setImage(teacherRq.getImage());
        }
        if (teacherRq.getPhoneNumber() != null) {
            userDetail.setPhone(teacherRq.getPhoneNumber());
        }
        if (teacherRq.getDob() != null) {
            userDetail.setDob(teacherRq.getDob());
        }
        if (teacherRq.getAddress() != null) {
            userDetail.setAddress(teacherRq.getAddress());
        }
        if (teacherRq.getAddress() != null) {
            userDetail.setGender(teacherRq.getGender());
        }
        if (teacherRq.getStatus() != null) {
            user.setStatus(Status.valueOf(teacherRq.getStatus()));
        }
    }
    public List<TeacherResponse> findTeachersByClassId(int classId) {
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));

        // Find all user subjects related to this class
        List<UserSubject> userSubjects = userSubjectRepository.findByClassroom(existingClass);

        // Filter for Users with role "TEACHER" and convert to TeacherResponse
        List<TeacherResponse> teachers = userSubjects.stream()
                .map(UserSubject::getUser) // Get User from UserSubject
                .filter(user -> user.getRole().getRoleName().equals("TEACHER")) // Filter for TEACHER role
                .map(this::convertToTeacherResponse) // Convert each User to TeacherResponse
                .collect(Collectors.toList()); // Collect to a List<TeacherResponse>

        return teachers;
    }





    private TeacherResponse convertToTeacherResponse(User user) {
        if (user == null) {
            return new TeacherResponse();
        }

        return new TeacherResponse(
                user.getId(),
                user.getEmail(),
                user.getUserDetail() != null ? user.getUserDetail().getFullName() : null,
                user.getUserDetail() != null ? user.getUserDetail().getPhone() : null,
                user.getUserDetail() != null ? user.getUserDetail().getAddress() : null,
                user.getUserDetail() != null ? user.getUserDetail().getDob() : null,
                user.getUserDetail() != null ? user.getUserDetail().getGender() : null,
                user.getRole().getRoleName(),
                user.getStatus().name(),
                user.getUserDetail() != null ? user.getUserDetail().getImage() : null,
                user.getCreatedAt()
        );
    }
}
