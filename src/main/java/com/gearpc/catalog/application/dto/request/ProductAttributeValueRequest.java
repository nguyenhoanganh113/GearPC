package com.gearpc.catalog.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductAttributeValueRequest(
        @NotNull(message = "ID thuộc tính không được để trống")
        UUID attributeDefinitionId,

        @NotBlank(message = "Giá trị thuộc tính không được để trống")
        String value
) {
    public ProductAttributeValueRequest {
        value = value == null || value.isBlank() ? null : value.strip();
    }
}
