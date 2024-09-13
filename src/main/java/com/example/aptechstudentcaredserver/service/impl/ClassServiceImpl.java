package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
import com.example.aptechstudentcaredserver.bean.response.StudentResponse;
import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.entity.GroupClass;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.enums.Status;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.ClassRepository;
import com.example.aptechstudentcaredserver.repository.GroupClassRepository;
import com.example.aptechstudentcaredserver.repository.UserCourseRepository;
import com.example.aptechstudentcaredserver.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final GroupClassRepository groupClassRepository;
    private final UserCourseRepository userCourseRepository;


    @Override
    public List<ClassResponse> findAllClass() {
        List<Class> listClass = classRepository.findAll();
        return listClass.stream()
                .map(this::convertToClassResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClassResponse findClassById(int classId) {
        Optional<Class> optionalClass = classRepository.findById(classId);

        if (optionalClass.isPresent()) {
            return convertToClassResponse(optionalClass.get());
        } else {
            throw new NotFoundException("Class not found with id: " + classId);
        }
    }

    @Override
    public void addClass(ClassRequest classRequest) {
        Class existingClass = classRepository.findByClassName(classRequest.getClassName());

        if (existingClass != null) {
            throw new DuplicateException("Class with this name already exists");
        }

        // Create new Class
        Class newClass = new Class();
        newClass.setClassName(classRequest.getClassName());
        newClass.setCenter(classRequest.getCenter());
        newClass.setHour(classRequest.getHour());
        newClass.setDays(classRequest.getDays());
        newClass.setStatus(Status.STUDYING);
        newClass.setCreatedAt(LocalDateTime.now());
        newClass.setUpdatedAt(LocalDateTime.now());

        // Save new Class
        classRepository.save(newClass);
    }

    @Override
    public ClassResponse updateClass(int classId, ClassRequest classRequest) {
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found with id " + classId));

        Class classWithNewName = classRepository.findByClassName(classRequest.getClassName());
        if (classWithNewName != null && classWithNewName.getId() != classId) {
            throw new DuplicateException("Class with name '" + classRequest.getClassName() + "' already exists.");
        }

        existingClass.setClassName(classRequest.getClassName());
        existingClass.setCenter(classRequest.getCenter());
        existingClass.setHour(classRequest.getHour());
        existingClass.setDays(classRequest.getDays());
        existingClass.setStatus(Status.valueOf(classRequest.getStatus()));

        classRepository.save(existingClass);

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

        List<StudentResponse> studentResponses = groupClasses.stream()
                .map(groupClass -> {
                    User user = groupClass.getUser();
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
                            classEntity.getClassName(),
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
                })
                .collect(Collectors.toList());

        return new ClassResponse(
                classEntity.getId(),
                classEntity.getClassName(),
                classEntity.getCenter(),
                classEntity.getHour(),
                classEntity.getDays(),
                classEntity.getCreatedAt(),
                classEntity.getStatus().name() != null ? classEntity.getStatus().name() : null,
                studentResponses
        );
    }
}