package com.example.aptechstudentcaredserver.bean.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassRequest {
    @NotBlank(message = "Class name is required")
    @Size(max = 100, message = "Class name must not exceed 100 characters")
    private String className;

    @NotBlank(message = "Center is required")
    @Size(max = 100, message = "Center must not exceed 100 characters")
    private String center;

    @NotBlank(message = "Hour is required")
    @Size(max = 50, message = "Hour must not exceed 50 characters")
    private String hour;

    @NotBlank(message = "Days are required")
    private String days;  // Consider changing this to a List<DayOfWeeks> if needed

    @NotBlank(message = "Status is required")
    @Size(max = 20, message = "Status must not exceed 20 characters")
    private String status;

    @NotBlank(message = "Semester is required")
    @Size(max = 10, message = "Semester must not exceed 10 characters")
    private String sem;

    @NotBlank(message = "Course code is required")
    @Size(max = 20, message = "Course code must not exceed 20 characters")
    private String courseCode;
}
