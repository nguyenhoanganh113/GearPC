package com.gearpc.dto.request;

import com.gearpc.entity.valueobject.enums.ProductStatus;
import com.gearpc.annotation.EnumPattern;

public record UpdateProductStatusRequest(
        @EnumPattern(fieldName = "productStatus", enumClass = ProductStatus.class)
        String productStatus
) {
}
