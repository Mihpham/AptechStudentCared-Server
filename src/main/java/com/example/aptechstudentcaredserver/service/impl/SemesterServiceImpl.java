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
        String[] semesterNames = {"sem1", "sem2", "sem3","sem4"};
        for (String name : semesterNames) {
            semesterRepository.findByName(name)
                    .orElseGet(() -> createSemester(name));
        }
    }

    private Semester createSemester(String name) {
        Semester semester = new Semester();
        semester.setName(name);
        semester.setStartDate("2024-01-01");
        semester.setEndDate("2024-06-30");

        return semesterRepository.save(semester);
    }
}
