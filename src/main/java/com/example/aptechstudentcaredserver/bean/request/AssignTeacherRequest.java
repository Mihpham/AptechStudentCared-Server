package com.example.aptechstudentcaredserver.bean.request;

import lombok.Data;

@Data
public class AssignTeacherRequest {
    private String subjectCode;
    private String teacherName;
    private String status;
}
