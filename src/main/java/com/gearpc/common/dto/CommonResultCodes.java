package com.gearpc.common.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CommonResultCodes implements ResultCode {

    SUCCESS("200", 200, "Thành công"),
    BAD_REQUEST("400", 400, "Yêu cầu không hợp lệ"),
    UNAUTHORIZED("401", 401, "Thông tin xác thực không hợp lệ"),
    FORBIDDEN("403", 403, "Không có quyền truy cập"),
    NOT_FOUND("404", 404, "Không tìm thấy yêu cầu"),
    METHOD_NOT_ALLOWED("405", 405, "Phương thức không được phép"),
    LOCKED("423", 423, "Yêu cầu thất bại, vui lòng thử lại sau"),
    TOO_MANY_REQUESTS("429", 429, "Yêu cầu quá thường xuyên, vui lòng thử lại sau"),
    INTERNAL_SERVER_ERROR("500", 500, "Lỗi hệ thống"),
    REPEATED_REQUESTS("900", 900, "Yêu cầu lặp lại, vui lòng thử lại sau"),
    DEMO_DENY("901", 901, "Chế độ demo, cấm thao tác ghi"),
    LIMIT_EXCEED_ERROR("501", 501, "Vượt quá giới hạn"),
    LOCK_ERROR("502", 502, "Lock error"),
    UNKNOWN("999", 500, "Lỗi không xác định");

    private final String code;
    private final int httpStatusCode;
    private final String message;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getKey() {
        return this.name();
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }
}
