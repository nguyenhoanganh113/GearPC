package com.gearpc.dto.response;

import com.gearpc.entity.valueobject.enums.AttributeDataType;

import java.util.UUID;

public record AttributeDefinitionOptionResponse(
        UUID id,
        String name,
        String code,
        String unit,
        AttributeDataType dataType
) {
}
