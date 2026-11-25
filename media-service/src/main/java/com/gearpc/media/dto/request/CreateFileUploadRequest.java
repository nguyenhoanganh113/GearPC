package com.gearpc.media.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateFileUploadRequest(
        @NotBlank String originalFileName,
        @NotBlank String contentType
) {
}
