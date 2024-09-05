package com.example.aptechstudentcaredserver.bean.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageRequest {
    @NotBlank
    private MultipartFile image;
}
