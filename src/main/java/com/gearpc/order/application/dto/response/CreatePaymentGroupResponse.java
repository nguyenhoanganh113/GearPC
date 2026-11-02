package com.gearpc.order.application.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CreatePaymentGroupResponse(
        UUID id,
        String name,
        String code,
        Integer sortOrder,
        Integer status,
        Instant createdAt
) {
}
