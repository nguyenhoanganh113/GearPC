package com.gearpc.catalog.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequest(
        @NotBlank(message = "Tên sản phẩm không được để trống")
        String name,
        String sku,
        String description,
        @NotNull(message = "Giá sản phẩm không được để trống")
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá sản phẩm phải lớn hơn 0")
        BigDecimal price,
        @NotNull(message = "Số lượng tồn kho không được để trống")
        @Min(value = 0, message = "Số lượng tồn kho không được là số âm")
        Integer stockQuantity,
        String images,
        @NotNull(message = "ID danh mục sản phẩm không được để trống")
        UUID categoryId,
        @NotNull(message = "ID thương hiệu không được để trống")
        UUID brandId
) {
    public CreateProductRequest {
        name = name != null ? name.strip() : null;
        sku = sku != null ? sku.strip() : null;
        description = description != null ? description.strip() : null;
        images = images != null ? images.strip() : null;
    }
}
