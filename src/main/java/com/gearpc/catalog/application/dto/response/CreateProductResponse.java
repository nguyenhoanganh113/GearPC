package com.gearpc.catalog.application.dto.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gearpc.catalog.domain.valueobject.enums.ProductStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateProductResponse(
        UUID id,
        String name,
        String slug,
        String sku,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        ProductStatus productStatus,
        String images,
        CategoryOptionResponse category,
        BrandOptionResponse brand,
        Instant createdAt,
        String createdBy
) {
}
