package com.gearpc.dto;

public record FieldErrorResponse(
        String field,
        String message
) {
}
