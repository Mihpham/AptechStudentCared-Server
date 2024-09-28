package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.AttendanceRequest;
import com.example.aptechstudentcaredserver.bean.response.AttendanceResponse;
import com.example.aptechstudentcaredserver.entity.Attendance;

import java.util.List;

public interface AttendanceService {
    public AttendanceResponse   updateOrCreateAttendance(int userId, int scheduleId, AttendanceRequest request);
    List<AttendanceResponse> findAllAttendances();

}


