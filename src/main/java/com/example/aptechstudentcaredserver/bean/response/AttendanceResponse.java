package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceResponse {
    private int id;
    private int scheduleId;
    private int studentId;
    private String studentName;
    private String attendanceStatus1;
    private String attendanceStatus2;
    private LocalDateTime checkin1;
    private LocalDateTime checkin2;
    private String note;
    private LocalDateTime createdAt;
    private LocalDate scheduleDate;
}
