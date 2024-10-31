package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectTeacherResponse {
    private int subjectId;
    private int teacherId;
    private String subjectCode;
    private String teacherName;
    private String status;
    private int numberOfSessions;
}
