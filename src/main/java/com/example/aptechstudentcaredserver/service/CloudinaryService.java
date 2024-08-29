package com.example.aptechstudentcaredserver.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadToCloudinary(MultipartFile file) throws IOException {
        // Kiểm tra nếu file không rỗng
        if (file.isEmpty()) {
            throw new IllegalArgumentException("The file is empty");
        }

        // Tải ảnh lên Cloudinary
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

        // Trả về URL của ảnh
        return uploadResult.get("url").toString();
    }
}
