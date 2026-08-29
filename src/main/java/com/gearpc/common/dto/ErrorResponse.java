package com.gearpc.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record ErrorResponse(
        long timestamp,
        int statusCode,       // HTTP Status Code (400, 404, 409...)
        int businessCode,     // Mã lỗi nghiệp vụ của dự án (1001, 2003...)
        String errorName,     // Tên Enum của mã lỗi ("BRAND_SLUG_EXISTS")
        String errorReason,   // Tên HTTP Status ("Conflict", "Bad Request")
        String message,       // Thông báo lỗi chi tiết
        String path,          // Endpoint URI gây ra lỗi
        List<FieldErrorResponse> details // Danh sách lỗi validation chi tiết (nếu có)
) {
}
