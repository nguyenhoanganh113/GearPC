package com.gearpc.catalog.application.dto.request;

import com.gearpc.catalog.domain.valueobject.enums.ProductStatus;
import com.gearpc.catalog.domain.valueobject.enums.SortBy;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSearchRequest(
        @Size(max = 150)
        String keyword,

        SortBy sortBy,

        UUID categoryId,
        UUID brandId,

        ProductStatus productStatus,

        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal minPrice,

        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal maxPrice,

        Boolean inStock
) {
    public ProductSearchRequest {
        keyword = stripToNull(keyword);
    }

    private static String stripToNull(String value) {
        return value == null || value.isBlank()
                ? null
                : value.strip();
    }
}
