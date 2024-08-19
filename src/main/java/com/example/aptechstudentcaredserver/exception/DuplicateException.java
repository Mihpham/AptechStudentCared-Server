package com.example.aptechstudentcaredserver.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DuplicateException extends RuntimeException{
    private final String message;
}
