package com.example.aptechstudentcaredserver.bean.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceRequest {
    private LocalDateTime checkin1;
    private LocalDateTime checkin2;
    private String attendanceStatus1;
    private String attendanceStatus2;
    private String note;
}
