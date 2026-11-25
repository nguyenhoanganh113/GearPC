package com.gearpc.common.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record FilePresignedUrlsRequest(
        @NotEmpty
        @Size(max = 100)
        List<@NotBlank String> fileIds
) {}
