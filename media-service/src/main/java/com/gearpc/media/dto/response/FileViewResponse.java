package com.gearpc.media.dto.response;

import java.time.Instant;
import java.util.UUID;

public record FileViewResponse(
        UUID fileId,
        String fileName,
        String contentType,
        long size,
        String presignedGetUrl,
        Instant expiresAt
) {}
