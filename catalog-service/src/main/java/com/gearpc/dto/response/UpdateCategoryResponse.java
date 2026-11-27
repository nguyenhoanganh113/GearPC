package com.gearpc.dto.response;

public record UpdateCategoryResponse(

        String name,

        String description,

        boolean active,

        String imageUrl

) {
}
