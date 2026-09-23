package com.gearpc.catalog.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gearpc.catalog.domain.valueobject.enums.AttributeDataType;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductAttributeValueResponse(
        UUID productId,
        UUID attributeDefinitionId,
        String attributeName,
        String attributeCode,
        String unit,
        AttributeDataType dataType,
        boolean required,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        String value
) {
}
