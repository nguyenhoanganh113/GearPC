package com.gearpc.catalog.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(

        @NotBlank(message = "Name is required")
        String name,

        String description,

        String imageUrl

) {
    public CreateCategoryRequest {
        name = name != null ? name.strip() : null;
        description = description != null ? description.strip() : null;
        imageUrl = imageUrl != null ? imageUrl.strip() : null;
    }
}
