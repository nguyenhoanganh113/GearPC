package com.gearpc.catalog.application.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(

        @Size(min = 1)
        String name,

        String description,

        String imageUrl

) {

    public UpdateCategoryRequest {
        name = name != null ? name.strip() : null;
        description = description != null ? description.strip() : null;
        imageUrl = imageUrl != null ? imageUrl.strip() : null;
    }

}
