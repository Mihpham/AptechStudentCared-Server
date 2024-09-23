package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.AttendanceRequest;
import com.example.aptechstudentcaredserver.bean.response.AttendanceResponse;
import com.example.aptechstudentcaredserver.entity.Attendance;
import com.example.aptechstudentcaredserver.entity.Schedule;
import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.repository.AttendanceRepository;
import com.example.aptechstudentcaredserver.repository.ScheduleRepository;
import com.example.aptechstudentcaredserver.repository.UserRepository;
import com.example.aptechstudentcaredserver.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Override
    public AttendanceResponse updateOrCreateAttendance(int userId, int scheduleId, AttendanceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        Attendance attendance = attendanceRepository.findByUserAndSchedule(user, schedule);

        if (attendance == null) {
            attendance = new Attendance();
            attendance.setUser(user);
            attendance.setSchedule(schedule);
            attendance.setCheckin1(LocalDateTime.now());
            attendance.setCheckin2(LocalDateTime.now());
            attendance.setAttendance1(request.getAttendanceStatus1());
            attendance.setAttendance2(request.getAttendanceStatus2());
            attendance.setNote(request.getNote());
            attendance.setCreatedAt(LocalDateTime.now());
        } else {
            attendance.setCheckin1(LocalDateTime.now());
            attendance.setCheckin2(LocalDateTime.now());
            attendance.setAttendance1(request.getAttendanceStatus1());
            attendance.setAttendance2(request.getAttendanceStatus2());
            attendance.setNote(request.getNote());
        }

        Attendance savedAttendance = attendanceRepository.save(attendance);

        // Tạo và trả về AttendanceResponse với thông tin ngày
        return new AttendanceResponse(
                savedAttendance.getId(),
                user.getUserDetail().getFullName(),
                savedAttendance.getAttendance1(),
                savedAttendance.getAttendance2(),
                savedAttendance.getCheckin1(),
                savedAttendance.getCheckin2(),
                savedAttendance.getNote(),
                savedAttendance.getCreatedAt(),
                schedule.getStartDate()
        );
    }

    @Override
    public List<Attendance> getAttendancesByScheduleId(int scheduleId) {
        return attendanceRepository.findByScheduleId(scheduleId);
    }


}
