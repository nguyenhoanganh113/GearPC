package com.gearpc.common.exception;

import com.gearpc.common.dto.ApiResponse;
import com.gearpc.common.dto.FieldErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException exception) {
        return buildErrorResponse(exception.getErrorCode());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception
    ) {
        return buildErrorResponse(ErrorCode.DATA_INTEGRITY_VIOLATION);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<FieldErrorResponse>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        List<FieldErrorResponse> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorResponse(error.getField(), error.getDefaultMessage()))
                .toList();

        return buildErrorResponse(ErrorCode.VALIDATION_FAILED, details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception
    ) {
        return buildErrorResponse(ErrorCode.MALFORMED_JSON);
    }

    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(ErrorCode errorCode) {
        return buildErrorResponse(errorCode, null);
    }

    private <T> ResponseEntity<ApiResponse<T>> buildErrorResponse(ErrorCode errorCode, T data) {
        ApiResponse<T> response = ApiResponse.error(
                String.valueOf(errorCode.getCode()),
                errorCode.getMessage(),
                data
        );

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }
}
