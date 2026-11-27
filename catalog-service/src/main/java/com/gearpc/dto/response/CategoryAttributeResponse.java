package com.gearpc.dto.response;

import com.gearpc.entity.valueobject.enums.AttributeDataType;

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
