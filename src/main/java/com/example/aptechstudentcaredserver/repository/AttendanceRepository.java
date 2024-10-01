package com.example.aptechstudentcaredserver.repository;

import com.example.aptechstudentcaredserver.entity.Attendance;
import com.example.aptechstudentcaredserver.entity.Schedule;
import com.example.aptechstudentcaredserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance,Integer> {
    Attendance findByUserAndSchedule(User user, Schedule schedule);
    List<Attendance> findByScheduleId(int scheduleId);
    List<Attendance> findByUserId(int userId);
    long countByUserIdAndSchedule_Classes_Id(int userId, int classId);

}
