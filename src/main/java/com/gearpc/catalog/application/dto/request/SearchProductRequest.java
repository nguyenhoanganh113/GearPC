package com.gearpc.catalog.application.dto.request;

import com.gearpc.catalog.domain.valueobject.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record SearchProductRequest(
        String keyword,
        UUID categoryId,
        UUID brandId,
        ProductStatus productStatus,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Boolean inStock
) {
}
