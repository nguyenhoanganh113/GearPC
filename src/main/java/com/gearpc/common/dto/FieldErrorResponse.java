package com.gearpc.common.dto;

public record FieldErrorResponse(
        String field,
        String message
) {
}
