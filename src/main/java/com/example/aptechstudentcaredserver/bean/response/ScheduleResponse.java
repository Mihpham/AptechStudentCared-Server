package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponse {
    private int scheduleId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String subjectName;
    private String className;
}
