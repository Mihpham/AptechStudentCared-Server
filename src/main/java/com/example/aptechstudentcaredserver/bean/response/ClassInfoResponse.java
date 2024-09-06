package com.example.aptechstudentcaredserver.bean.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClassInfoResponse {
    private String className;
    private String center;
    private String hour;
    private String days;
    private LocalDate admissionDate;
    private String status;

}
