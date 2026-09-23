package com.gearpc.catalog.application.dto.response;

import com.gearpc.catalog.domain.valueobject.enums.AttributeDataType;

import java.util.UUID;

public record AttributeDefinitionOptionResponse(
        UUID id,
        String name,
        String code,
        String unit,
        AttributeDataType dataType
) {
}
