package com.gearpc.catalog.application.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record BrandResponse(

        String name,

        String slug,

        String logoUrl,

        boolean active,

        Instant createdAt,

        Instant lastModifiedAt

) {
}
