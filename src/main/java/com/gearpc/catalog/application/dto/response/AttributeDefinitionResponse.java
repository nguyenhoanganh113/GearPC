package com.gearpc.catalog.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gearpc.catalog.domain.valueobject.enums.AttributeDataType;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AttributeDefinitionResponse(
        UUID id,
        String name,
        String code,
        String unit,
        AttributeDataType dataType,
        boolean active,
        Instant createdAt,
        String createdBy,
        Instant lastModifiedAt,
        String lastModifiedBy
) {
}
