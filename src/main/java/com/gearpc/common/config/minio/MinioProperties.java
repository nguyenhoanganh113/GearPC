package com.gearpc.common.config.minio;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "minio")
@Validated
public record MinioProperties(
        @NotBlank
        String endpoint,

        @NotNull
        @Valid
        Access access,

        @NotNull
        @Valid
        Bucket bucket,

        @NotNull
        @Valid
        Folder folder
) {
        public record Access(@NotBlank String name, @NotBlank String secret) {}
        public record Bucket(@NotBlank String name) {}
        public record Folder(@NotBlank String name) {}
}
