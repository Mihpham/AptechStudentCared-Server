package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.ScheduleRequest;
import com.example.aptechstudentcaredserver.bean.response.ScheduleResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {
    ScheduleResponse getScheduleById(int scheduleId);

    public ScheduleResponse updateScheduleById(int scheduleId, ScheduleRequest request);

    List<ScheduleResponse> getSchedulesByClassAndSubjectId(int classId, int subjectId);

    List<ScheduleResponse> createSchedule(ScheduleRequest scheduleRq, int classId, int subjectId);

    List<ScheduleResponse> updateSchedule(ScheduleRequest scheduleRq, int classId, int subjectId);

    public void deleteScheduleById(int scheduleId);
}
