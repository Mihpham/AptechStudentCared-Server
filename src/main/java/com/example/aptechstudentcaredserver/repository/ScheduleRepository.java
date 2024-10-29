package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByClassesId(int classId);
    List<Schedule> findBySubjectId(int subjectId);

    List<Schedule> findByClassesIdAndSubjectId(int classId, int subjectId);

    List<Schedule> findByStartDateAndClassesId(LocalDate startDate, int classId);
}
