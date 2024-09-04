package com.example.aptechstudentcaredserver.bean.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubjectRequest {
    @NotNull(message = "Subject name cannot be null")
    private String subjectName;

    @NotBlank(message = "Subject code cannot be blank")
    private String subjectCode;

    @NotNull(message = "Total hours cannot be null")
    private Integer totalHours;

}
