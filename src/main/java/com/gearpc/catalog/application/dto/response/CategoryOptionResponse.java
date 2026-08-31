package com.gearpc.catalog.application.dto.response;

import java.util.UUID;

public record CategoryOptionResponse(
        UUID id,
        String name
) {
}
