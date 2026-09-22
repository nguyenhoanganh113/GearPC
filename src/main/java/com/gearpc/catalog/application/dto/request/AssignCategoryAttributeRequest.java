package com.gearpc.catalog.application.dto.request;

import java.util.UUID;

public record AssignCategoryAttributeRequest(
        UUID attributeDefinitionId,
        boolean required
) {
}
