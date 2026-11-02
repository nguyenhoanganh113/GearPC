package com.gearpc.order.application.dto.request;

import jakarta.validation.constraints.*;

public record CreatePaymentGroupRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 100) String code,
        @PositiveOrZero Integer sortOrder,
        @NotNull Integer status
) {
}
