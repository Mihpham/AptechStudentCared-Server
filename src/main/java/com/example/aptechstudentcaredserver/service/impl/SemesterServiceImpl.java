package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.entity.Semester;
import com.example.aptechstudentcaredserver.repository.SemesterRepository;
import com.example.aptechstudentcaredserver.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SemesterServiceImpl implements SemesterService {

    private  final SemesterRepository semesterRepository;

    @Override
    public void initializeDefaultSemesters() {
        String[] semesterNames = {"Sem1", "Sem2", "Sem3","Sem4"};
        for (String name : semesterNames) {
            semesterRepository.findByName(name)
                    .orElseGet(() -> createSemester(name));
        }
    }

    private Semester createSemester(String name) {
        Semester semester = new Semester();
        semester.setName(name);
        return semesterRepository.save(semester);
    }
}
