package com.gearpc.common.entity;

import com.gearpc.common.enums.StoredFileStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "stored_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoredFile extends BaseEntity {

    @Column(name = "bucket", nullable = false, length = 63)
    private String bucket;

    @Column(name = "object_key", nullable = false, length = 1024)
    private String objectKey;

    @Column(name = "temporary_object_key", length = 1024)
    private String temporaryObjectKey;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "content_type", nullable = false, length = 255)
    private String contentType;

    // Populated from object metadata after the upload has been verified.
    @Column(name = "size")
    private Long size;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    @Builder.Default
    private StoredFileStatus status = StoredFileStatus.PENDING;

    @Column(name = "upload_expired_at", nullable = false)
    private Instant uploadExpiredAt;

    @Column(name = "ready_at")
    private Instant readyAt;
}
