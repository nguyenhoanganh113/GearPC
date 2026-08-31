package com.gearpc.catalog.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateBrandResponse(

        UUID id,

        String name,

        String slug,

        String logoUrl,

        boolean active,

        Instant lastModifiedAt,

        String lastModifiedBy

) {
}
