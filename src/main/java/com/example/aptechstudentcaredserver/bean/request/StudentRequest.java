package com.example.aptechstudentcaredserver.bean.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequest {

    private String image;

//    @NotBlank(message = "Roll number is required")
//    private String rollNumber;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Class name is required")
    private String className;

    @NotBlank(message = "Date of birth is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date of birth must be in the format yyyy-MM-dd")
    private String dob;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10,15}$", message = "Phone number must be between 10 and 15 digits")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Courses are required")
    private Set<String> courses;

    @NotBlank(message = "Status is required")
    private String status;

    @NotBlank(message = "Parent's full name is required")
    private String parentFullName;

    @NotBlank(message = "Student relation is required")
    private String studentRelation;

    @NotBlank(message = "Parent's phone number is required")
    @Pattern(regexp = "^\\d{10,15}$", message = "Parent's phone number must be between 10 and 15 digits")
    private String parentPhone;

    @NotBlank(message = "Parent's gender is required")
    private String parentGender;
}

