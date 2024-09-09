package com.example.aptechstudentcaredserver.bean.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClassRequest {
    private String className;
    private String center;
    private String hour;
    private String days;
    private String status;
}
