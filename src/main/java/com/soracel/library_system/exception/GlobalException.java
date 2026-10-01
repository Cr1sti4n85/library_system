package com.soracel.library_system.exception;

import com.soracel.library_system.payload.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(GenreException.class)
    public ResponseEntity<ApiResponse> handleGenreException(GenreException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiResponse(ex.getMessage(), false)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .getFirst()
                .getDefaultMessage();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(message, false));
    }
}
