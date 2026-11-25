package com.gearpc.media.dto.response;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record CreateFileUploadResponse(
        UUID fileId,
        String presignedUploadUrl,
        Instant expiresAt,
        String method,
        Map<String, String> headers
) {
}
