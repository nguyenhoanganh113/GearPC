package com.gearpc.media.exception;

import com.gearpc.media.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class FileExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppException(AppException exception) {
        return error(exception.getErrorCode(), exception.getData());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<FieldError>>> handleValidation(MethodArgumentNotValidException exception) {
        var details = exception.getBindingResult().getFieldErrors().stream()
                .map(field -> new FieldError(field.getField(), field.getDefaultMessage()))
                .toList();
        return error(ErrorCode.VALIDATION_FAILED, details);
    }

    private <T> ResponseEntity<ApiResponse<T>> error(ErrorCode error, T data) {
        return ResponseEntity.status(error.getHttpStatus())
                .body(new ApiResponse<>(String.valueOf(error.getCode()), error.getMessage(), data));
    }

    public record FieldError(String field, String message) {
    }
}
