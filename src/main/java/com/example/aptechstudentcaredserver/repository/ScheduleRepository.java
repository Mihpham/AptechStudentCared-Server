package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Integer> {
    List<Schedule> findByClassesId(int classId);

    List<Schedule> findByClassesIdAndSubjectId(int classId, int subjectId); // Thêm phương thức này

}
