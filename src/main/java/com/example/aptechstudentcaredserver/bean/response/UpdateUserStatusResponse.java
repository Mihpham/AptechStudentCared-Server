package com.example.aptechstudentcaredserver.bean.response;

import com.example.aptechstudentcaredserver.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UpdateUserStatusResponse {
    private String message;
    private Status updatedStatus;
}
