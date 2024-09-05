package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponse {
    private int id;
    private String className;
    private String center;
    private String hour;
    private String days;
    private LocalDate admissionDate;
    private String status;
}
