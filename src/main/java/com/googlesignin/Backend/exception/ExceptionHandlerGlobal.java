package com.googlesignin.Backend.exception;

import com.googlesignin.Backend.models.response.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerGlobal {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleExceptions(RuntimeException exception){
        ApiError error = new ApiError(exception.getMessage(), "001", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleExceptions(Exception exception){
        ApiError error = new ApiError(exception.getMessage(), "001", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
