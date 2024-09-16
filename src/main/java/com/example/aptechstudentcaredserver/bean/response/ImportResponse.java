package com.example.aptechstudentcaredserver.bean.response;

import com.example.aptechstudentcaredserver.bean.request.StudentRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportResponse {
        private String message;
        private int rowNumber;
}
