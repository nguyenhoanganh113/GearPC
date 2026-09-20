package com.gearpc.catalog.application.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(

        @Size(min = 1, message = "Tên danh mục phải nhiều hơn 1 ký tự")
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
