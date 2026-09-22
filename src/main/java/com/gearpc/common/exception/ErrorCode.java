package com.gearpc.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    VALIDATION_FAILED(1001, "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),
    MALFORMED_JSON(1002, "JSON không hợp lệ hoặc không thể đọc được", HttpStatus.BAD_REQUEST),
    DATA_INTEGRITY_VIOLATION(1003, "Dữ liệu không hợp lệ hoặc vi phạm ràng buộc hệ thống", HttpStatus.BAD_REQUEST),
    INVALID_PRICE_RANGE(1004, "Khoảng giá không hợp lệ", HttpStatus.BAD_REQUEST),

    BRAND_NOT_FOUND(2001, "Thương hiệu không thể tìm thấy!", HttpStatus.NOT_FOUND),
    BRAND_EXISTS(2002, "Tên thương hiệu đã tồn tại", HttpStatus.CONFLICT),

    CATEGORY_NOT_FOUND(2201, "Danh mục không thể tìm thấy!", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTS(2202, "Tên danh mục đã tồn tại", HttpStatus.CONFLICT),

    PRODUCT_NOT_FOUND(2401, "Sản phẩm không thể tìm thấy!", HttpStatus.NOT_FOUND),
    PRODUCT_EXISTS(2402, "Sản phẩm đã tồn tại", HttpStatus.CONFLICT),
    INVALID_PRODUCT_STATUS(2403, "Trạng thái sản phẩm không hợp lệ", HttpStatus.BAD_REQUEST),
    PRODUCT_ALREADY_DELETED(2404, "Sản phẩm đã bị xóa", HttpStatus.BAD_REQUEST),

    ATTRIBUTE_DEFINITION_NOT_FOUND(2501, "Thuộc tính không thể tìm thấy!", HttpStatus.NOT_FOUND),
    CATEGORY_ATTRIBUTE_EXISTS(2502, "Thuộc tính đã tồn tại", HttpStatus.CONFLICT),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

}
