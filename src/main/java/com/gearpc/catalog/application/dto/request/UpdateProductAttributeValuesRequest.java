package com.gearpc.catalog.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateProductAttributeValuesRequest(
        @NotNull(message = "Danh sách giá trị thuộc tính không được để trống")
        List<@NotNull(message = "Giá trị thuộc tính không được để trống") @Valid ProductAttributeValueRequest> attributes
) {
}
