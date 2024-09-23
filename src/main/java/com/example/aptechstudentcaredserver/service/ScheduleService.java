package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.ScheduleRequest;
import com.example.aptechstudentcaredserver.entity.Schedule;

import java.util.List;

public interface ScheduleService {
    Schedule getScheduleById(int scheduleId); // Thêm phương thức mới

    List<Schedule> getSchedulesByClassId(int classId);

    List<Schedule> createSchedule(ScheduleRequest scheduleRq, int classId);

    List<Schedule> updateSchedule(ScheduleRequest scheduleRq, int classId);

}
