package com.example.aptechstudentcaredserver.bean.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private String note;
}
