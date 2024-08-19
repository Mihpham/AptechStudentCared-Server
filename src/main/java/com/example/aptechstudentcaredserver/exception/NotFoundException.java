package com.example.aptechstudentcaredserver.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotFoundException extends RuntimeException {
    private final String message;
}
