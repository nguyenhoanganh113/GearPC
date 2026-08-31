package com.gearpc.catalog.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateBrandRequest(
        @NotBlank(message = "Name is required")
        String name,

        String logoUrl
) {
}
