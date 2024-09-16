package com.example.aptechstudentcaredserver.bean.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassRequest {
    private String className;
    private String center;
    private String hour;
    private String days;
    private String status;
    private String sem;
    private String teacherName;
    private String courseCode;
}
