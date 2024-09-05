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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
        List<User> users = userRepository.findByRoleRoleName("STUDENT");
        return users.stream()
                .map(user -> convertToStudentResponse(user, findGroupClassByUserId(user.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public StudentResponse findStudentById(int studentId) {
        User user = findUserById(studentId);
        GroupClass groupClass = findGroupClassByUserId(studentId);
        return convertToStudentResponse(user, groupClass);
    }

    @Override
    public void createStudent(StudentRequest studentRq) {
        Class studentClass = classRepository.findByClassName(studentRq.getClassName());
        if (studentClass == null) {
            throw new IllegalArgumentException("Class not found. Please add the class before adding a student.");
        }

        Role role = findOrCreateRole("STUDENT");
        String email = emailGeneratorService.generateUniqueEmail(studentRq.getFullName());

        User user = createUser(studentRq, role, email);
        userRepository.save(user);

        UserDetail userDetail = createUserDetail(studentRq, user);
        userDetailRepository.save(userDetail);

        createOrUpdateParent(studentRq, userDetail);
        createUserCourses(studentRq, user);
        createGroupClass(studentClass, user);
    }


    @Override
    public StudentResponse updateStudent(int studentId, StudentRequest studentRq) {
        User user = findUserById(studentId);
        UserDetail userDetail = user.getUserDetail();

        updateUserDetails(user, userDetail, studentRq);
        updateClass(studentId, studentRq);
        updateCourses(studentId, studentRq);
        updateGroupClassStatus(studentId, studentRq);
        updateParentDetails(userDetail, studentRq);

        userRepository.save(user);
        userDetailRepository.save(userDetail);

        GroupClass groupClass = findGroupClassByUserId(studentId);
        return convertToStudentResponse(user, groupClass);
    }

    @Override
    public void deleteStudent(int studentId) {
        // Tìm User theo studentId
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id " + studentId));

        // Xóa các bản ghi trong UserCourse
        List<UserCourse> userCourses = userCourseRepository.findByUserId(studentId);
        userCourseRepository.deleteAll(userCourses);

        // Xóa bản ghi trong GroupClass
        GroupClass groupClass = groupClassRepository.findByUserId(studentId)
                .orElseThrow(() -> new NotFoundException("Group class not found for user id " + studentId));
        groupClassRepository.delete(groupClass);

        // Cập nhật hoặc xóa bản ghi trong Parent
        UserDetail userDetail = user.getUserDetail();
        if (userDetail != null && userDetail.getParent() != null) {
            Parent parent = userDetail.getParent();
            userDetail.setParent(null); // Xóa liên kết với Parent
            userDetailRepository.save(userDetail); // Cập nhật UserDetail

            // Xóa Parent nếu không còn liên kết với bất kỳ User nào khác
            if (parentRepository.findById(parent.getId()).isPresent()) {
                parentRepository.delete(parent);
            }
        }

        // Xóa User
        userRepository.deleteById(studentId);
    }


    private User findUserById(int studentId) {
        return userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("User not found with id " + studentId));
    }

    private GroupClass findGroupClassByUserId(int studentId) {
        return groupClassRepository.findByUserId(studentId)
                .orElseThrow(() -> new NotFoundException("Group class not found for user id " + studentId));
    }

    private Role findOrCreateRole(String roleName) {
        return Optional.ofNullable(roleRepository.findByRoleName(roleName))
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleName(roleName);
                    return roleRepository.save(role);
                });
    }

    private User createUser(StudentRequest studentRq, Role role, String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("@123456789"));
        user.setRole(role);
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private UserDetail createUserDetail(StudentRequest studentRq, User user) {
        UserDetail userDetail = new UserDetail();
        userDetail.setRollNumber(studentRq.getRollNumber());
        userDetail.setImage(
                (studentRq.getImage() != null && !studentRq.getImage().isEmpty())
                        ? studentRq.getImage()
                        : null
        );
        userDetail.setFullName(studentRq.getFullName());
        userDetail.setGender(studentRq.getGender());
        userDetail.setDob(studentRq.getDob());
        userDetail.setPhone(studentRq.getPhoneNumber());
        userDetail.setAddress(studentRq.getAddress());
        userDetail.setUser(user);
        return userDetail;
    }

    private void createOrUpdateParent(StudentRequest studentRq, UserDetail userDetail) {
        Parent parent = Optional.ofNullable(userDetail.getParent()).orElseGet(() -> {
            Parent newParent = new Parent();
            newParent.setCreatedAt(LocalDateTime.now());
            return newParent;
        });

        if (studentRq.getParentFullName() != null) parent.setFullName(studentRq.getParentFullName());
        if (studentRq.getParentPhone() != null) parent.setPhone(studentRq.getParentPhone());
        if (studentRq.getParentGender() != null) parent.setGender(studentRq.getParentGender());
        if (studentRq.getStudentRelation() != null) parent.setStudentRelation(studentRq.getStudentRelation());

        parent.setUpdatedAt(LocalDateTime.now());
        parentRepository.save(parent);
        userDetail.setParent(parent);
    }

    private void createUserCourses(StudentRequest studentRq, User user) {
        if (studentRq.getCourses() != null) {
            studentRq.getCourses().forEach(courseName -> {
                // Find course by name, return Optional<Course>
                Course course = Optional.ofNullable(courseRepository.findByCourseName(courseName.trim()))
                        .orElseThrow(() -> new NotFoundException("Course not found: " + courseName.trim()));

                UserCourse userCourse = new UserCourse();
                userCourse.setUser(user);
                userCourse.setCourse(course);
                userCourse.setStartDate(LocalDateTime.now());
                userCourse.setEndDate(LocalDateTime.now().plusMonths(6));
                userCourse.setCreatedAt(LocalDateTime.now());
                userCourse.setUpdatedAt(LocalDateTime.now());
                userCourseRepository.save(userCourse);
            });
        }
    }



    private void createGroupClass(Class studentClass, User user) {
        GroupClass groupClass = new GroupClass();
        groupClass.setUser(user);
        groupClass.setClasses(studentClass);
        groupClass.setJoinedDate(LocalDateTime.now());
        groupClass.setStatus(ClassMemberStatus.STUDYING);
        groupClassRepository.save(groupClass);
    }

    private void updateUserDetails(User user, UserDetail userDetail, StudentRequest studentRq) {
        if (studentRq.getFullName() != null) userDetail.setFullName(studentRq.getFullName());
        if (studentRq.getImage() != null) userDetail.setImage(studentRq.getImage());
        if (studentRq.getEmail() != null) user.setEmail(studentRq.getEmail());
        if (studentRq.getRollNumber() != null) userDetail.setRollNumber(studentRq.getRollNumber());
        if (studentRq.getPhoneNumber() != null) userDetail.setPhone(studentRq.getPhoneNumber());
    }

    private void updateClass(int  studentId, StudentRequest studentRq) {
        if (studentRq.getClassName() != null) {
            Class studentClass = classRepository.findByClassName(studentRq.getClassName());
            if (studentClass == null) {
                throw new NotFoundException("Class not found. Please add the class before updating.");
            }

            GroupClass groupClass = findGroupClassByUserId(studentId);
            groupClass.setClasses(studentClass);
            groupClassRepository.save(groupClass);
        }
    }


    private void updateCourses(int studentId, StudentRequest studentRq) {
        if (studentRq.getCourses() != null) {
            List<UserCourse> existingCourses = userCourseRepository.findByUserId(studentId);
            userCourseRepository.deleteAll(existingCourses); // Remove all existing courses

            // Create new courses (only for existing ones)
            createUserCourses(studentRq, findUserById(studentId));
        }
    }


    private void updateGroupClassStatus(int studentId, StudentRequest studentRq) {
        if (studentRq.getStatus() != null) {
            GroupClass groupClass = findGroupClassByUserId(studentId);
            groupClass.setStatus(ClassMemberStatus.valueOf(studentRq.getStatus()));
            groupClassRepository.save(groupClass);
        }
    }

    private void updateParentDetails(UserDetail userDetail, StudentRequest studentRq) {
        if (studentRq.getParentFullName() != null || studentRq.getParentPhone() != null ||
                studentRq.getParentGender() != null || studentRq.getStudentRelation() != null
        ) {
            createOrUpdateParent(studentRq, userDetail);
        }
    }

    private StudentResponse convertToStudentResponse(User user, GroupClass groupClass) {
        if (user == null || groupClass == null) {
            return new StudentResponse(); // return object empty if need
        }

        Class studentClass = groupClass.getClasses();
        List<String> courses = userCourseRepository.findByUserId(user.getId()).stream()
                .map(userCourse -> userCourse.getCourse().getCourseName())
                .collect(Collectors.toList());

        return new StudentResponse(
                user.getId(),
                user.getUserDetail() != null ? user.getUserDetail().getImage() : null,
                user.getUserDetail() != null ? user.getUserDetail().getRollNumber() : null,
                user.getUserDetail() != null ? user.getUserDetail().getFullName() : null,
                user.getEmail(),
                user.getUserDetail() != null ? user.getUserDetail().getAddress() : null,
                studentClass != null ? studentClass.getClassName() : null,
                user.getUserDetail() != null ? user.getUserDetail().getGender() : null,
                user.getUserDetail() != null ? user.getUserDetail().getDob() : null,
                user.getUserDetail() != null ? user.getUserDetail().getPhone() : null,
                courses,
                groupClass.getStatus() != null ? groupClass.getStatus().name() : null,
                user.getUserDetail() != null ? user.getUserDetail().getParent().getFullName() : null,
                user.getUserDetail() != null ? user.getUserDetail().getParent().getStudentRelation() : null,
                user.getUserDetail() != null ? user.getUserDetail().getParent().getPhone() : null,
                user.getUserDetail() != null ? user.getUserDetail().getParent().getGender() : null

        );
    }

}
