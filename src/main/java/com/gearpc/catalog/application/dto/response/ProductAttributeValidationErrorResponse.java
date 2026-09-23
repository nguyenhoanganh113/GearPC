package com.gearpc.catalog.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gearpc.catalog.domain.valueobject.enums.ProductAttributeValidationReason;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductAttributeValidationErrorResponse(
        UUID attributeDefinitionId,
        String attributeCode,
        String attributeName,
        String rejectedValue,
        ProductAttributeValidationReason reason
) {
}
