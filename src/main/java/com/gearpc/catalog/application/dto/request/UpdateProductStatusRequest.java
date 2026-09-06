package com.gearpc.catalog.application.dto.request;

import com.gearpc.catalog.domain.valueobject.enums.ProductStatus;
import com.gearpc.common.annotation.EnumPattern;

public record UpdateProductStatusRequest(
        @EnumPattern(fieldName = "productStatus", enumClass = ProductStatus.class)
        String productStatus
) {
}
