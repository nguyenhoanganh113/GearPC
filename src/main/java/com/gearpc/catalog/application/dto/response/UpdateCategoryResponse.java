package com.gearpc.catalog.application.dto.response;

public record UpdateCategoryResponse(

        String name,

        String description,

        boolean active,

        String imageUrl

) {
}
