package com.gearpc.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignCategoryAttributeRequest(
        @NotNull(message = "ID thuộc tính không được để trống")
        UUID attributeDefinitionId,

        @NotNull(message = "Trạng thái bắt buộc không được để trống")
        Boolean required
) {
}
