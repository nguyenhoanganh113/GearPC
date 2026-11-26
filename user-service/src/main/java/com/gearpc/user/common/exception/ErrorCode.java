package com.gearpc.user.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    VALIDATION_FAILED(1001, "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),
    MALFORMED_JSON(1002, "JSON không hợp lệ hoặc không thể đọc được", HttpStatus.BAD_REQUEST),
    DATA_INTEGRITY_VIOLATION(1003, "Dữ liệu không hợp lệ hoặc vi phạm ràng buộc hệ thống", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(4002, "Người dùng không thể tìm thấy!", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS(4003, "Email hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED),
    EMAIL_NOT_VERIFIED(4004, "Tài khoản chưa xác minh email", HttpStatus.FORBIDDEN),

    TOKEN_EXPIRED(5001, "Token đã hết hạn", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(5002, "Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    MISSING_LOGOUT_INFO(5003, "Thông tin đăng xuất bị thiếu", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

}
