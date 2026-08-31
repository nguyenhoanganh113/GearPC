package com.gearpc.catalog.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DetailCategoryResponse(
        UUID id,
        String name,
        String slug,
        String description,
        String imageUrl,
        boolean active,
        Instant createdAt,
        Instant createdBy,
        Instant lastModifiedAt,
        Instant lastModifiedBy
) {
}
