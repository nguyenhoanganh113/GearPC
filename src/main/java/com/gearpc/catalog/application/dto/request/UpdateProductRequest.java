package com.gearpc.catalog.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequest(

        // Cho phép null, nhưng nếu đã gửi chuỗi thì không được rỗng và không được toàn khoảng trắng
        @Pattern(regexp = "^(?!\\s*$).+", message = "Tên sản phẩm không được chỉ chứa khoảng trắng")
        String name,

        @Pattern(regexp = "^(?!\\s*$).+", message = "SKU không được chỉ chứa khoảng trắng")
        String sku,

        String description,

        @DecimalMin(value = "0.00")
        BigDecimal price,

        @Min(value = 0, message = "Số lượng tồn kho không được âm")
        Integer stockQuantity,

        String images,

        UUID categoryId,

        UUID brandId

) {
    public UpdateProductRequest {
        name = stripToNull(name);
        sku = stripToNull(sku);
        description = stripToNull(description);
        images = stripToNull(images);
    }
    private static String stripToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}
