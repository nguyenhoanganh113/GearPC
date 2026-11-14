package com.gearpc.order.application.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CreatePaymentMethodResponse(
        UUID id,
        String name,
        String code,
        Integer status,
        String icon,
        Integer type,
        Boolean canViewAnonymous,
        Instant createdAt
) {
}
