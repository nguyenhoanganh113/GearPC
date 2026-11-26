package com.gearpc.user.common.dto;

public record FieldErrorResponse(
        String field,
        String message
) {
}
