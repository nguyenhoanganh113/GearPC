package com.gearpc.catalog.application.dto.request;

import com.gearpc.catalog.domain.valueobject.enums.AttributeDataType;
import jakarta.validation.constraints.Pattern;

public record UpdateAttributeDefinitionRequest(
        @Pattern(regexp = "^(?!\\s*$).+", message = "Tên thuộc tính không được chỉ chứa khoảng trắng")
        String name,

        @Pattern(regexp = "^(?!\\s*$).+", message = "Mã thuộc tính không được chỉ chứa khoảng trắng")
        String code,

        String unit,

        AttributeDataType dataType
) {
    public UpdateAttributeDefinitionRequest {
        name = stripToNull(name);
        code = stripToNull(code);
        unit = stripToNull(unit);
    }

    private static String stripToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}
