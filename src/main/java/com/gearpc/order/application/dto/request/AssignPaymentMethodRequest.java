package com.gearpc.order.application.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record AssignPaymentMethodRequest(
        @NotNull UUID methodId,
        @DecimalMin("0") @DecimalMax("100") @Digits(integer = 3, fraction = 2) BigDecimal discountPercent,
        @DecimalMin("0") @Digits(integer = 13, fraction = 2) BigDecimal maxDiscountAmount,
        @DecimalMin("0") @Digits(integer = 36, fraction = 2) BigDecimal minAmount,
        @DecimalMin("0") @DecimalMax("100") @Digits(integer = 3, fraction = 2) BigDecimal extraFeePercent,
        @DecimalMin("0") @Digits(integer = 36, fraction = 2) BigDecimal extraFeeAmount,
        @Size(max = 255) String description,
        @PositiveOrZero Integer sortOrder
) {
}
