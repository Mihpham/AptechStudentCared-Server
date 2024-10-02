package com.example.aptechstudentcaredserver.bean.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ScheduleRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String note;
}
