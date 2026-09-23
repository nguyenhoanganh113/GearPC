package com.gearpc.catalog.application.dto.response;

import com.gearpc.catalog.domain.valueobject.enums.AttributeDataType;

import java.util.UUID;

public record CategoryAttributeResponse(
        UUID categoryId,
        String categoryName,
        UUID attributeDefinitionId,
        String attributeName,
        String attributeCode,
        String unit,
        AttributeDataType attributeDataType,
        boolean required
) {
}
