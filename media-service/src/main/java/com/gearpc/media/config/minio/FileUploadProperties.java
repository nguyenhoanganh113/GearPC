package com.gearpc.media.config.minio;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.util.Locale;

@ConfigurationProperties(prefix = "app.file-upload")
@Validated
public record FileUploadProperties(
        @DefaultValue("1800")
        @Min(1)
        @Max(604800)
        int presignedPutUrlExpirySeconds,

        @DefaultValue("86400")
        @Min(1)
        int sessionTtlSeconds,

        @DefaultValue("10485760")
        @Min(1) long maxImageFileSize,

        /*
        @DefaultValue("104857600")
        @Min(1)
        long maxVideoFileSize,
         */

        @DefaultValue("1800")
        @Min(1)
        @Max(3600)
        int presignedGetUrlExpirySeconds,

        @DefaultValue("52428800")
        @Min(1)
        long maxFileSize
) {
    public long effectiveSessionTtlSeconds() {
        return Math.max(sessionTtlSeconds, presignedPutUrlExpirySeconds);
    }

    public long maxFileSizeForContentType(String contentType) {
        String type = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
        if (type.startsWith("image/")) return maxImageFileSize;
        //if (type.startsWith("video/")) return maxVideoFileSize;
        return maxFileSize;
    }
}
