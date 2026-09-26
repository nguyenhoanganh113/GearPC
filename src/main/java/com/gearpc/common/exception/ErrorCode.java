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
    BRAND_INACTIVE(2003, "Thương hiệu chưa được kích hoạt", HttpStatus.CONFLICT),

    CATEGORY_NOT_FOUND(2201, "Danh mục không thể tìm thấy!", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTS(2202, "Tên danh mục đã tồn tại", HttpStatus.CONFLICT),
    CATEGORY_INACTIVE(2203, "Danh mục chưa được kích hoạt", HttpStatus.CONFLICT),

    PRODUCT_NOT_FOUND(2401, "Sản phẩm không thể tìm thấy!", HttpStatus.NOT_FOUND),
    PRODUCT_EXISTS(2402, "Sản phẩm đã tồn tại", HttpStatus.CONFLICT),
    INVALID_PRODUCT_STATUS(2403, "Trạng thái sản phẩm không hợp lệ", HttpStatus.BAD_REQUEST),
    PRODUCT_CATEGORY_CHANGE_NOT_ALLOWED(2405, "Không thể đổi danh mục khi sản phẩm đã có giá trị thuộc tính", HttpStatus.CONFLICT),

    ATTRIBUTE_DEFINITION_NOT_FOUND(2601, "Thuộc tính không thể tìm thấy!", HttpStatus.NOT_FOUND),
    ATTRIBUTE_DEFINITION_EXISTS(2602, "Mã thuộc tính đã tồn tại", HttpStatus.CONFLICT),
    ATTRIBUTE_DEFINITION_INACTIVE(2603, "Thuộc tính chưa được kích hoạt", HttpStatus.BAD_REQUEST),

    CATEGORY_ATTRIBUTE_NOT_FOUND(2801, "Thuộc tính chưa được gán cho danh mục", HttpStatus.NOT_FOUND),
    CATEGORY_ATTRIBUTE_EXISTS(2802, "Thuộc tính đã tồn tại", HttpStatus.CONFLICT),
    CATEGORY_ATTRIBUTE_IN_USE(2803, "Không thể bỏ thuộc tính vì đang được sản phẩm sử dụng", HttpStatus.CONFLICT),

    PRODUCT_ATTRIBUTE_NOT_ALLOWED(3001, "Thuộc tính không áp dụng cho danh mục của sản phẩm", HttpStatus.BAD_REQUEST),
    REQUIRED_PRODUCT_ATTRIBUTE_MISSING(3002, "Thiếu thuộc tính bắt buộc của sản phẩm", HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_ATTRIBUTE_VALUE(3003, "Giá trị thuộc tính sản phẩm không đúng kiểu dữ liệu", HttpStatus.BAD_REQUEST),
    DUPLICATE_PRODUCT_ATTRIBUTE(3004, "Thuộc tính sản phẩm bị trùng lặp", HttpStatus.BAD_REQUEST),
    PRODUCT_ATTRIBUTE_VALIDATION_FAILED(3005, "Một hoặc nhiều thuộc tính sản phẩm không hợp lệ", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

}
