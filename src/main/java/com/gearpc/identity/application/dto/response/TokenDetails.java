package com.gearpc.identity.application.dto.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record TokenDetails(
        String value, // Token string
        String jti,
        Instant expiresAt,
        long ttl
) {
}
