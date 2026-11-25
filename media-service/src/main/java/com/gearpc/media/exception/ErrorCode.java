package com.gearpc.media.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    VALIDATION_FAILED(1001, "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_TYPE_INVALID(6001, "File upload không hợp lệ", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_NOT_FOUND(6002, "File upload not found", HttpStatus.NOT_FOUND),
    FILE_UPLOAD_NOT_READY(6003, "File upload is not ready", HttpStatus.CONFLICT),
    FILE_UPLOAD_SIZE_EXCEEDED(6005, "Invalid uploaded file size", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_REFERENCE_INVALID(6006, "Uploaded file metadata does not match", HttpStatus.BAD_REQUEST),
    FILE_STORAGE_ERROR(6008, "Cannot access file storage", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
