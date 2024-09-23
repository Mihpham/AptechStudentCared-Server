package com.example.aptechstudentcaredserver.bean.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ScheduleRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
