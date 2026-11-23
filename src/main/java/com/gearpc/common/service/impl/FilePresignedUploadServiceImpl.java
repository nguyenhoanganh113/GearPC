package com.gearpc.common.service.impl;

import com.gearpc.common.config.minio.FileUploadProperties;
import com.gearpc.common.config.minio.MinioProperties;
import com.gearpc.common.dto.request.CreateFileUploadRequest;
import com.gearpc.common.dto.request.ConfirmFileUploadRequest;
import com.gearpc.common.dto.response.CreateFileUploadResponse;
import com.gearpc.common.dto.response.FileViewResponse;
import com.gearpc.common.entity.StoredFile;
import com.gearpc.common.enums.StoredFileStatus;
import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import com.gearpc.common.repository.StoredFileRepository;
import com.gearpc.common.service.FilePresignedUploadService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http;
import io.minio.MinioClient;
import io.minio.CopyObjectArgs;
import io.minio.SourceObject;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilePresignedUploadServiceImpl implements FilePresignedUploadService {

    private static final Map<String, Set<String>> ALLOWED_CONTENT_TYPES = Map.of(
            "image/jpeg", Set.of("jpg", "jpeg"),
            "image/png", Set.of("png")
    );

    private final MinioProperties minioProperties;
    private final FileUploadProperties fileUploadProperties;
    private final MinioClient minioClient;
    private final StoredFileRepository storedFileRepository;

    @Override
    @Transactional(rollbackFor = MinioException.class)
    public CreateFileUploadResponse createPresignedUploadUrl(CreateFileUploadRequest request) throws MinioException {

        /*
         * Validate request
         * */
        if (!StringUtils.hasText(request.originalFileName()) || request.originalFileName().length() > 255) {
            throw new AppException(ErrorCode.VALIDATION_FAILED); // Validate original file name
        }

        if (!StringUtils.hasText(request.contentType()) || request.contentType().length() > 127) {
            throw new AppException(ErrorCode.FILE_UPLOAD_TYPE_INVALID); // Validate content type
        }
        MediaType mediaType = MediaType.parseMediaType(request.contentType());
        // Validate content type chứa ký tự *(wildcard) hay không
        if (mediaType.isWildcardType() || mediaType.isWildcardSubtype()) {
            throw new AppException(ErrorCode.FILE_UPLOAD_TYPE_INVALID); // Validate content type
        }

        if (ALLOWED_CONTENT_TYPES.containsKey(request.contentType())
                && !extension(request.originalFileName()).isEmpty()
                && !ALLOWED_CONTENT_TYPES.get(request.contentType()).contains(extension(request.originalFileName()))
        ) {
            throw new AppException(ErrorCode.FILE_UPLOAD_TYPE_INVALID);
        }

        StoredFile storedFile = new StoredFile();
        storedFile.setBucket(minioProperties.bucket().name());
        storedFile.setObjectKey(
                (minioProperties.folder().name() + "/uploads/") +
                        (LocalDate.now() + "/payment_" + UUID.randomUUID() + "/") +
                        (extension(request.originalFileName().isEmpty() ? "" : "." + extension(request.originalFileName())))
                );
        storedFile.setTemporaryObjectKey(
                minioProperties.folder().name() + "/tmp/uploads/" +
                        (LocalDate.now() + "/payment_" + UUID.randomUUID() + "/") +
                        (extension(request.originalFileName().isEmpty() ? "" : "." + extension(request.originalFileName())))
                );
        storedFile.setFileName(request.originalFileName());
        storedFile.setContentType(request.contentType());
        storedFile.setStatus(StoredFileStatus.PENDING);
        storedFile.setUploadExpiredAt(Instant.now().plusSeconds(fileUploadProperties.presignedPutUrlExpirySeconds()));
        storedFile.setCreatedBy(actor());
        storedFile.setLastModifiedBy(actor());

        storedFile = storedFileRepository.saveAndFlush(storedFile);

        Map<String, String> headers = Map.of(
                "Content-Type", storedFile.getContentType(),
                "x-amz-meta-file-id", storedFile.getId().toString()
        );

        String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Http.Method.PUT)
                        .bucket(storedFile.getBucket())
                        .object(storedFile.getTemporaryObjectKey())
                        .expiry(fileUploadProperties.presignedPutUrlExpirySeconds(), TimeUnit.SECONDS)
                        .extraHeaders(headers)
                        .build()
        );

        return new CreateFileUploadResponse(
                storedFile.getId(),
                url,
                storedFile.getUploadExpiredAt(),
                "PUT",
                headers
        );
    }

    @Override
    @Transactional(noRollbackFor = AppException.class)
    public FileViewResponse confirmUpload(ConfirmFileUploadRequest request) {
        if (request.fileId() == null) {
            throw new AppException(ErrorCode.VALIDATION_FAILED);
        }
        StoredFile file = storedFileRepository.findForUpdate(request.fileId())
                .orElseThrow(() -> new AppException(ErrorCode.FILE_UPLOAD_NOT_FOUND));
        if (file.getDeletedAt() != null || file.getStatus() == StoredFileStatus.DELETED) {
            throw new AppException(ErrorCode.FILE_UPLOAD_NOT_FOUND);
        }
        if (!Objects.equals(file.getCreatedBy(), actor())) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FORBIDDEN);
        }
        if (file.getStatus() == StoredFileStatus.EXPIRED) {
            throw new AppException(ErrorCode.FILE_UPLOAD_EXPIRED);
        }
        if (file.getStatus() == StoredFileStatus.PENDING) {
            if (file.getUploadExpiredAt() == null || !file.getUploadExpiredAt().isAfter(Instant.now())) {
                file.setStatus(StoredFileStatus.EXPIRED);
                file.setLastModifiedBy(actor());
                storedFileRepository.saveAndFlush(file);
                removeAfterCommit(file.getBucket(), file.getTemporaryObjectKey());
                throw new AppException(ErrorCode.FILE_UPLOAD_EXPIRED);
            }
            finalizeUpload(file);
        }
        if (file.getStatus() != StoredFileStatus.READY || file.getReadyAt() == null
                || file.getSize() == null || file.getSize() <= 0) {
            throw new AppException(ErrorCode.FILE_UPLOAD_NOT_READY);
        }
        return signedView(file);
    }

    private void finalizeUpload(StoredFile file) {
        StatObjectResponse uploaded = stat(file.getBucket(), file.getTemporaryObjectKey());
        if (!StringUtils.hasText(uploaded.etag())) {
            throw new AppException(ErrorCode.FILE_STORAGE_ERROR);
        }
        try {
            validateUploadedObject(file, uploaded);
        } catch (AppException e) {
            file.setStatus(StoredFileStatus.FAILED);
            file.setLastModifiedBy(actor());
            storedFileRepository.saveAndFlush(file);
            removeAfterCommit(file.getBucket(), file.getTemporaryObjectKey());
            throw e;
        }

        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(file.getBucket())
                    .object(file.getObjectKey())
                    .source(SourceObject.builder()
                            .bucket(file.getBucket())
                            .object(file.getTemporaryObjectKey())
                            .versionId(uploaded.versionId())
                            .matchETag(uploaded.etag())
                            .build())
                    .build());
        } catch (MinioException e) {
            throw storageFailure("Cannot finalize file upload", e);
        }
        StatObjectResponse finalized = stat(file.getBucket(), file.getObjectKey());
        if (finalized.size() != uploaded.size()
                || !Objects.equals(finalized.contentType(), uploaded.contentType())
                || !file.getId().toString().equals(finalized.userMetadata().getFirst("file-id"))) {
            throw new AppException(ErrorCode.FILE_STORAGE_ERROR);
        }
        file.setSize(finalized.size());
        file.setContentType(finalized.contentType());
        file.setStatus(StoredFileStatus.READY);
        file.setReadyAt(Instant.now());
        file.setLastModifiedBy(actor());
        storedFileRepository.saveAndFlush(file);
        removeAfterCommit(file.getBucket(), file.getTemporaryObjectKey());
    }

    private void validateUploadedObject(StoredFile file, StatObjectResponse uploaded) {
        if (!ALLOWED_CONTENT_TYPES.containsKey(file.getContentType())
                || !Objects.equals(file.getContentType(), uploaded.contentType())) {
            throw new AppException(ErrorCode.FILE_UPLOAD_TYPE_INVALID);
        }
        if (uploaded.size() <= 0
                || uploaded.size() > fileUploadProperties.maxFileSizeForContentType(uploaded.contentType())) {
            throw new AppException(ErrorCode.FILE_UPLOAD_SIZE_EXCEEDED);
        }
        if (!file.getId().toString().equals(uploaded.userMetadata().getFirst("file-id"))) {
            throw new AppException(ErrorCode.FILE_UPLOAD_REFERENCE_INVALID);
        }
    }

    private FileViewResponse signedView(StoredFile file) {
        try {
            String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Http.Method.GET)
                    .bucket(file.getBucket())
                    .object(file.getObjectKey())
                    .expiry(fileUploadProperties.presignedGetUrlExpirySeconds(), TimeUnit.SECONDS)
                    .build());
            var params = UriComponentsBuilder.fromUriString(url).build().getQueryParams();
            Instant signedAt = Instant.from(DateTimeFormatter.ofPattern("uuuuMMdd'T'HHmmssX", Locale.ROOT)
                    .parse(Objects.requireNonNull(params.getFirst("X-Amz-Date"))));
            Instant expiresAt = signedAt.plusSeconds(Long.parseLong(params.getFirst("X-Amz-Expires")));
            return new FileViewResponse(file.getId(), file.getFileName(), file.getContentType(),
                    file.getSize(), url, expiresAt);
        } catch (MinioException | IllegalArgumentException | java.time.DateTimeException | NullPointerException e) {
            throw storageFailure("Cannot create file view URL", e);
        }
    }

    private StatObjectResponse stat(String bucket, String key) {
        if (!StringUtils.hasText(key)) {
            throw new AppException(ErrorCode.FILE_UPLOAD_NOT_FOUND);
        }
        try {
            return minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(key).build());
        } catch (ErrorResponseException e) {
            if (Set.of("NoSuchKey", "NoSuchObject", "NotFound").contains(e.errorResponse().code())) {
                throw new AppException(ErrorCode.FILE_UPLOAD_NOT_FOUND);
            }
            throw storageFailure("Cannot read uploaded file", e);
        } catch (MinioException e) {
            throw storageFailure("Cannot read uploaded file", e);
        }
    }

    private AppException storageFailure(String message, Exception e) {
        log.error(message, e);
        return new AppException(ErrorCode.FILE_STORAGE_ERROR);
    }

    private void removeAfterCommit(String bucket, String key) {
        if (!StringUtils.hasText(key)) return;
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    removeQuietly(bucket, key);
                }
            });
        } else {
            removeQuietly(bucket, key);
        }
    }

    private void removeQuietly(String bucket, String key) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(key).build());
        } catch (MinioException e) {
            log.warn("Cannot remove temporary upload: bucket={}, key={}", bucket, key, e);
        }
    }

    // chuyển đổi phần sau dấu . thành lower case
    private String extension(String name) {
        int dot = name.lastIndexOf('.');
        return dot < 0 ? "" : name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private String actor() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? "ADMIN" : authentication.getName();
    }
}
