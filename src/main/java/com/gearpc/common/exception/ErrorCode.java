package com.gearpc.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    DATA_INTEGRITY_VIOLATION(1003, "Dữ liệu không hợp lệ hoặc vi phạm ràng buộc hệ thống", HttpStatus.BAD_REQUEST),

    BRAND_NOT_FOUND(2001, "Thương hiệu không thể tìm thấy!", HttpStatus.NOT_FOUND),
    BRAND_EXISTS(2002, "Tên thương hiệu đã tồn tại", HttpStatus.CONFLICT);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

}
