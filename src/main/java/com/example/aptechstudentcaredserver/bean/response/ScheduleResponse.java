package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponse {
    private int scheduleId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String subjectCode;
    private String className;
    private String status;
    private String note;
}
