package com.example.aptechstudentcaredserver.handler;


import com.example.aptechstudentcaredserver.bean.response.ErrorResponse;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException notFoundException) {
        return new ErrorResponse(HttpStatus.NO_CONTENT.value(), notFoundException.getMessage());
    }

    @ExceptionHandler(DuplicateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDuplicateException(DuplicateException duplicateException) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), duplicateException.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        Map<String, String> errors = new HashMap<String, String>();
        methodArgumentNotValidException.getBindingResult().getFieldErrors().
                forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return errors;
    }
}
