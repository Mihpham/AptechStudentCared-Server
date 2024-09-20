package com.example.aptechstudentcaredserver.bean.request;

import com.example.aptechstudentcaredserver.enums.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassRequest {
    private String className;
    private String center;
    private String hour;
    private List<DayOfWeek> days;
    private String status;
    private String sem;
    private String courseCode;
}
