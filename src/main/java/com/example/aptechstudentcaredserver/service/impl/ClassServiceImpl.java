package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.ClassRequest;
import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.repository.ClassRepository;
import com.example.aptechstudentcaredserver.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private  final ClassRepository classRepository;
    @Override
    public void addClass(ClassRequest classRequest) {
        Class existingClass = classRepository.findByClassName(classRequest.getClassName());
        if (existingClass != null) {
            throw new RuntimeException("Class with this name already exists");
        }

        // Create new Class
        Class newClass = new Class();
        newClass.setClassName(classRequest.getClassName());
        newClass.setCreatedAt(LocalDateTime.now());
        newClass.setUpdatedAt(LocalDateTime.now());

        // Save new Class
        classRepository.save(newClass);
    }
}
