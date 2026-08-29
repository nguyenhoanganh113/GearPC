package com.gearpc.common.exception;

import com.gearpc.common.dto.ErrorResponse;
import com.gearpc.common.dto.FieldErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(
            AppException ex,
            WebRequest request
    ) {
        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse response = buildErrorResponse(errorCode, request, null);

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            WebRequest request
    ) {
        ErrorCode errorCode = ErrorCode.DATA_INTEGRITY_VIOLATION;
        ErrorResponse errorResponse = buildErrorResponse(errorCode, request, null);
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    private ErrorResponse buildErrorResponse(
            ErrorCode errorCode,
            WebRequest request,
            List<FieldErrorResponse> details
    ) {
        return ErrorResponse.builder()
                .timestamp(new Date().getTime())
                .statusCode(errorCode.getHttpStatus().value()) // HTTP Status Code: 400, 404, 409...
                .businessCode(errorCode.getCode()) // Mã lỗi nghiệp vụ dự án: 1001, 2001, 2002...
                .errorName(errorCode.name()) // Enum String Name: BRAND_SLUG_EXISTS..
                .errorReason(errorCode.getHttpStatus().getReasonPhrase()) // Conflict, bad request, ....
                .message(errorCode.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .details(details)
                .build();
    }

}
