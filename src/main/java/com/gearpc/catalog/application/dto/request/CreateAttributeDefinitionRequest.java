package com.gearpc.catalog.application.dto.request;

import com.gearpc.catalog.domain.valueobject.enums.AttributeDataType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAttributeDefinitionRequest(
        @NotBlank(message = "Tên thuộc tính không được để trống")
        String name,

        @NotBlank(message = "Mã thuộc tính không được để trống")
        String code,

        String unit,

        @NotNull(message = "Kiểu dữ liệu không được để trống")
        AttributeDataType dataType
) {
    public CreateAttributeDefinitionRequest {
        name = stripToNull(name);
        code = stripToNull(code);
        unit = stripToNull(unit);
    }

    private static String stripToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}
