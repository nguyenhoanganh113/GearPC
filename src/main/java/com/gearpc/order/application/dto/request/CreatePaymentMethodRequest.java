package com.gearpc.order.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePaymentMethodRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 100) String code,
        @NotNull Integer status,
        @Size(max = 255) String icon,
        @NotNull Integer type,
        Boolean canViewAnonymous
) {
}
