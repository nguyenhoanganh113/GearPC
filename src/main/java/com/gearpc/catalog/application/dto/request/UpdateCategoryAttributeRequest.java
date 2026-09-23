package com.gearpc.catalog.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateCategoryAttributeRequest(
        @NotNull(message = "Trạng thái bắt buộc không được để trống")
        Boolean required
) {
}
