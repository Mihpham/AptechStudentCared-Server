package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.bean.response.ClassResponse;
import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.enums.DayOfWeeks;
import com.example.aptechstudentcaredserver.enums.Status;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.ClassRepository;
import com.example.aptechstudentcaredserver.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;

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
            throw new RuntimeException("Class with this name already exists");
        }

        Class newClass = new Class();

        newClass.setClassName(classRequest.getClassName());
        newClass.setCenter(classRequest.getCenter());
        newClass.setHour(classRequest.getHour());
        newClass.setDays(parseDays(classRequest.getDays()));
        newClass.setAdmissionDate(classRequest.getAdmissionDate());
        newClass.setStatus(Status.STUDYING);
        newClass.setCreatedAt(LocalDateTime.now());
        newClass.setUpdatedAt(LocalDateTime.now());

        classRepository.save(newClass);
    }

    private List<DayOfWeeks> parseDays(String daysString) {
        String[] daysArray = daysString.split(",");
        List<DayOfWeeks> dayOfWeeksList = new ArrayList<>();

        for (String dayStr : daysArray) {
            int dayValue = Integer.parseInt(dayStr.trim()); // Trim any spaces
            dayOfWeeksList.add(DayOfWeeks.fromValue(dayValue));
        }

        return dayOfWeeksList;
    }


    @Override
    public ClassResponse updateClass(int classId, ClassRequest classRequest) {
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found with id " + classId));

        existingClass.setClassName(classRequest.getClassName());
        existingClass.setCenter(classRequest.getCenter());
        existingClass.setHour(classRequest.getHour());
        existingClass.setDays(parseDays(classRequest.getDays()));
        existingClass.setAdmissionDate(classRequest.getAdmissionDate());
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
        String days = classEntity.getDays() != null ?
                classEntity.getDays().stream()
                        .map(DayOfWeeks::name)  // Convert enum to its name (e.g., "MONDAY")
                        .collect(Collectors.joining(","))
                : null;

        return new ClassResponse(
                classEntity.getId(),
                classEntity.getClassName(),
                classEntity.getCenter(),
                classEntity.getHour(),
                days,
                classEntity.getAdmissionDate(),
                classEntity.getStatus() != null ? classEntity.getStatus().name() : null
        );
    }
}
