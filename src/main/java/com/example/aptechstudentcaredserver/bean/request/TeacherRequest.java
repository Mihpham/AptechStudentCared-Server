package com.example.aptechstudentcaredserver.bean.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherRequest {
    private String image;

    @NotBlank(message = "Full name cannot be empty")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be a valid format (10-15 digits)")
    private String phoneNumber;

    @NotBlank(message = "Date of birth cannot be empty")
    private String dob; // Bạn có thể xem xét sử dụng LocalDate cho ngày sinh

    @NotBlank(message = "Gender cannot be empty")
    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other")
    private String gender;

    private String address; // Có thể thêm kiểm tra nếu cần

    @NotBlank(message = "Status cannot be empty")
    private String status; // Bạn có thể thêm các kiểm tra khác nếu cần
}
