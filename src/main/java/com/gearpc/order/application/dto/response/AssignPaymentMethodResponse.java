package com.gearpc.order.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AssignPaymentMethodResponse(
        UUID id, UUID groupId, UUID methodId,
        BigDecimal discountPercent, BigDecimal maxDiscountAmount, BigDecimal minAmount,
        BigDecimal extraFeePercent, BigDecimal extraFeeAmount,
        String description, Integer sortOrder, Long version, Instant createdAt
) {
}
