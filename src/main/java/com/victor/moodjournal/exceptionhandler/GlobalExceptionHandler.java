package com.victor.moodjournal.exceptionhandler;

import com.victor.moodjournal.dto.ApiError;
import com.victor.moodjournal.exception.EmailAlreadyExistsException;
import com.victor.moodjournal.exception.MoodEntryCreationException;
import com.victor.moodjournal.exception.MoodEntryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MoodEntryNotFoundException.class)
    public ResponseEntity<ApiError> handleMoodEntryNotFound(MoodEntryNotFoundException ex){

        ApiError apiError = new ApiError(404, ex.getMessage(), getClassName(ex), LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(MoodEntryCreationException.class)
    public ResponseEntity<ApiError> handleMoodEntryNotFound(MoodEntryCreationException ex){
        ApiError apiError = new ApiError(409, ex.getMessage(),getClassName(ex), LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleMoodEntryNotFound(EmailAlreadyExistsException ex){
        ApiError apiError = new ApiError(409, ex.getMessage(),getClassName(ex), LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericExceptions(Exception ex){
        ApiError apiError = new ApiError(500, ex.getMessage(),getClassName(ex), LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    private static String getClassName(Exception ex){
        StackTraceElement stackTraceElement = ex.getStackTrace()[0];
        return stackTraceElement.getClassName();
    }

}
