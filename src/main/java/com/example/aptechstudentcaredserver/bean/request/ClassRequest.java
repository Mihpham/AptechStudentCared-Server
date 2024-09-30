package com.example.aptechstudentcaredserver.bean.request;

import com.example.aptechstudentcaredserver.enums.DayOfWeeks;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassRequest {
    private String className;
    private String center;
    private LocalTime startHour;
    private LocalTime endHour;
    private List<DayOfWeeks> days;
    private String status;
    private String sem;
    private String courseCode;
}
