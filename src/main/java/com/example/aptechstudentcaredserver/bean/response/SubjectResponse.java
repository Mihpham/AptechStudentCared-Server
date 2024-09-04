package com.example.aptechstudentcaredserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubjectResponse {
        private int id;
        private String subjectName;
        private String subjectCode;
        private int totalHours;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
}
