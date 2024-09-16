package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
import com.example.aptechstudentcaredserver.bean.response.CourseResponse;
import com.example.aptechstudentcaredserver.bean.response.StudentResponse;
import com.example.aptechstudentcaredserver.bean.response.TeacherResponse;
import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.entity.*;
import com.example.aptechstudentcaredserver.enums.ClassMemberStatus;
import com.example.aptechstudentcaredserver.enums.Status;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.exception.EmptyListException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.*;
import com.example.aptechstudentcaredserver.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final GroupClassRepository groupClassRepository;
    private final UserCourseRepository userCourseRepository;
    private final CourseRepository courseRepository;
    private final CourseSubjectRepository courseSubjectRepository;
    private final SemesterRepository semesterRepository;
    private final UserDetailRepository userDetailRepository;

    @Override
    public List<ClassResponse> findAllClass() {
        List<Class> listClass = classRepository.findAll();
        if (listClass.isEmpty()) {
            throw new EmptyListException("No classes found.");
        }
        return listClass.stream()
                .map(this::convertToClassResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClassResponse findClassById(int classId) {
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found with id: " + classId));

        return convertToClassResponse(existingClass);
    }

    @Override
    public void addClass(ClassRequest classRequest) {
        Class existingClass = classRepository.findByClassName(classRequest.getClassName());

        if (existingClass != null) {
            throw new DuplicateException("Class with this name already exists");
        }

        Class newClass = new Class();
        newClass.setClassName(classRequest.getClassName());
        newClass.setCenter(classRequest.getCenter());
        newClass.setHour(classRequest.getHour());
        newClass.setDays(classRequest.getDays());
        newClass.setStatus(Status.STUDYING);
        Course course = courseRepository.findByCourseCode(classRequest.getCourseCode());
        if (course == null) {
            throw new NotFoundException("Course not found with code: " + classRequest.getCourseCode());
        }
        newClass.setCourse(course);

        Semester semester = semesterRepository.findByName("Sem1")
                .orElseThrow(() -> new NotFoundException("Default semester 'Sem1' not found."));
        newClass.setSemester(semester);

        newClass.setCreatedAt(LocalDateTime.now());
        newClass.setUpdatedAt(LocalDateTime.now());


        classRepository.save(newClass);
    }

    @Override
    public ClassResponse updateClass(int classId, ClassRequest classRequest) {
        // Tìm lớp học hiện tại theo ID
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found with id " + classId));

        // Cập nhật thông tin lớp học
        existingClass.setClassName(classRequest.getClassName());
        existingClass.setCenter(classRequest.getCenter());
        existingClass.setHour(classRequest.getHour());
        existingClass.setDays(classRequest.getDays());
        existingClass.setStatus(Status.valueOf(classRequest.getStatus()));

        // Tìm khóa học và học kỳ
        Course course = courseRepository.findByCourseCode(classRequest.getCourseCode());
        if (course == null) {
            throw new NotFoundException("Course not found with code: " + classRequest.getCourseCode());
        }
        existingClass.setCourse(course);

        if (classRequest.getSem() != null && !classRequest.getSem().isEmpty()) {
            Semester semester = semesterRepository.findByName(classRequest.getSem())
                    .orElseThrow(() -> new NotFoundException("Semester not found with name: " + classRequest.getSem()));
            existingClass.setSemester(semester);
        } else {
            existingClass.setSemester(null);
        }

        existingClass.setUpdatedAt(LocalDateTime.now());
        classRepository.save(existingClass);

        // Xử lý giáo viên
        if (classRequest.getTeacherName() != null && !classRequest.getTeacherName().trim().isEmpty()) {
            // Tìm thông tin giáo viên mới
            UserDetail teacherDetail = userDetailRepository.findByFullName(classRequest.getTeacherName())
                    .orElseThrow(() -> new NotFoundException("Teacher not found with name: " + classRequest.getTeacherName()));

            User teacher = teacherDetail.getUser();
            if (teacher == null) {
                throw new NotFoundException("No user associated with the teacher details.");
            }

            // Xóa giáo viên cũ khỏi nhóm lớp nếu có
            List<GroupClass> existingGroupClasses = groupClassRepository.findByClassesId(classId);
            for (GroupClass groupClass : existingGroupClasses) {
                if (groupClass.getUser() != null &&
                        groupClass.getUser().getUserDetail() != null &&
                        !groupClass.getUser().getUserDetail().getFullName().equals(classRequest.getTeacherName())) {
                    // Xóa giáo viên cũ
                    groupClassRepository.delete(groupClass);
                }
            }

            // Kiểm tra xem giáo viên mới đã tồn tại trong lớp học chưa
            boolean teacherExistsInClass = existingGroupClasses.stream()
                    .anyMatch(gc -> gc.getUser() != null &&
                            gc.getUser().getUserDetail() != null &&
                            gc.getUser().getUserDetail().getFullName().equals(classRequest.getTeacherName()));

            if (!teacherExistsInClass) {
                // Thêm giáo viên mới vào nhóm lớp
                GroupClass newGroupClass = new GroupClass();
                newGroupClass.setUser(teacher);
                newGroupClass.setClasses(existingClass);
                newGroupClass.setJoinedDate(LocalDateTime.now());
                newGroupClass.setStatus(ClassMemberStatus.STUDYING);

                groupClassRepository.save(newGroupClass);
            }
        } else {
            // Nếu không có tên giáo viên mới, xóa tất cả giáo viên khỏi lớp
            List<GroupClass> existingGroupClasses = groupClassRepository.findByClassesId(classId);
            existingGroupClasses.stream()
                    .filter(groupClass -> groupClass.getUser() != null)
                    .forEach(groupClassRepository::delete);
        }

        return convertToClassResponse(existingClass);
    }







    @Override
    public void deleteClass(int classId) {
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found with id " + classId));

        classRepository.delete(existingClass);
    }

    private ClassResponse convertToClassResponse(Class classEntity) {
        List<GroupClass> groupClasses = groupClassRepository.findByClassesId(classEntity.getId());

        // Lấy thông tin khóa học
        Course course = classEntity.getCourse();
        CourseResponse courseResponse = null;
        if (course != null) {
            List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(course.getId());

            Map<String, List<String>> semesterSubjects = courseSubjects.stream()
                    .collect(Collectors.groupingBy(
                            cs -> cs.getSemester().getName(),
                            Collectors.mapping(cs -> cs.getSubject().getSubjectName(), Collectors.toList())
                    ));

            courseResponse = new CourseResponse(
                    course.getId(),
                    course.getCourseName(),
                    course.getCourseCode(),
                    course.getCourseCompTime(),
                    semesterSubjects
            );
        }

        // Lấy thông tin học kỳ
        Semester semester = classEntity.getSemester();
        String semesterName = null;
        if (semester != null) {
            semesterName = semester.getName();
        }

        // Tạo danh sách sinh viên chỉ bao gồm người dùng có vai trò là "STUDENT"
        List<StudentResponse> studentResponses = groupClasses.stream()
                .filter(groupClass -> groupClass.getUser() != null
                        && groupClass.getUser().getRole() != null
                        && groupClass.getUser().getRole().getRoleName().equals("STUDENT"))
                .map(groupClass -> {
                    User user = groupClass.getUser();
                    List<String> courses = userCourseRepository.findByUserId(user.getId()).stream()
                            .map(userCourse -> userCourse.getCourse().getCourseName())
                            .collect(Collectors.toList());

                    UserDetail userDetail = user.getUserDetail();

                    // Xử lý thông tin phụ huynh, kiểm tra null
                    String parentFullName = null;
                    String parentRelation = null;
                    String parentPhone = null;
                    String parentGender = null;

                    if (userDetail != null && userDetail.getParent() != null) {
                        Parent parent = userDetail.getParent();
                        parentFullName = parent.getFullName();
                        parentRelation = parent.getStudentRelation();
                        parentPhone = parent.getPhone();
                        parentGender = parent.getGender();
                    }

                    return new StudentResponse(
                            user.getId(),
                            userDetail != null ? userDetail.getImage() : null,
                            userDetail != null ? userDetail.getRollNumber() : null,
                            userDetail != null ? userDetail.getFullName() : null,
                            user.getEmail(),
                            userDetail != null ? userDetail.getAddress() : null,
                            classEntity.getClassName(),
                            userDetail != null ? userDetail.getGender() : null,
                            userDetail != null ? userDetail.getDob() : null,
                            userDetail != null ? userDetail.getPhone() : null,
                            courses,
                            groupClass.getStatus() != null ? groupClass.getStatus().name() : null,
                            parentFullName,
                            parentRelation,
                            parentPhone,
                            parentGender
                    );
                })
                .collect(Collectors.toList());

        // Lấy thông tin giáo viên từ bảng GroupClass
        String teacherName = groupClasses.stream()
                .map(groupClass -> {
                    User user = groupClass.getUser();
                    UserDetail userDetail = user.getUserDetail();
                    return userDetail != null ? userDetail.getFullName() : null;
                })
                .filter(name -> name != null)
                .findFirst()
                .orElse(null);

        // Tạo và trả về đối tượng ClassResponse
        return new ClassResponse(
                classEntity.getId(),
                classEntity.getClassName(),
                classEntity.getCenter(),
                classEntity.getHour(),
                classEntity.getDays(),
                classEntity.getCreatedAt(),
                classEntity.getStatus().name(),
                semesterName,
                teacherName,
                courseResponse,
                studentResponses
        );
    }

}
