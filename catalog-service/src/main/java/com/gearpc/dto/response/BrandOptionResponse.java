package com.gearpc.dto.response;

import java.util.UUID;

public record BrandOptionResponse(
    UUID id,
    String name,
    String slug
) {
}
