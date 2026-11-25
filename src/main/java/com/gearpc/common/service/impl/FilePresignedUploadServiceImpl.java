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
import com.gearpc.common.util.ImageFilePolicy;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
    private static final DateTimeFormatter AMZ_DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuuMMdd'T'HHmmssX", Locale.ROOT);

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
        StoredFile storedFile = lockedUpload(request.fileId());

        if (storedFile.getDeletedAt() != null) {
            throw new AppException(ErrorCode.FILE_UPLOAD_NOT_FOUND);
        }
        if (storedFile.getStatus() == StoredFileStatus.PENDING) {
            finalizeUpload(storedFile);
        }

        return readyLogoView(storedFile);
    }

    @Override
    @Transactional(readOnly = true)
    public FileViewResponse presignedGetInternal(String fileId) {
        if (!StringUtils.hasText(fileId)) {
            throw new AppException(ErrorCode.FILE_UPLOAD_REFERENCE_INVALID);
        }
        validateId(fileId);
        StoredFile storedFile = storedFileRepository.findById(UUID.fromString(fileId))
                .orElseThrow(() -> new AppException(ErrorCode.FILE_UPLOAD_NOT_FOUND));

        return readyLogoView(storedFile);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, FileViewResponse> presignedGetsInternal(Collection<String> fileIds) {
        Set<String> ids = new LinkedHashSet<>();
        for (String id : fileIds) {
            if (StringUtils.hasText(id)) {
                validateId(id);
                ids.add(id);
            }
        }
        if (ids.isEmpty()) {
            return Map.of();
        }
        List<StoredFile> foundFiles = storedFileRepository.findAllById(
                ids.stream().map(UUID::fromString).toList());
        if (foundFiles.size() != ids.size()) {
            throw new AppException(ErrorCode.FILE_UPLOAD_NOT_FOUND);
        }
        Map<String, StoredFile> files = new HashMap<>();
        for (StoredFile storedFile : foundFiles) {
            files.put(storedFile.getId().toString(), storedFile);
        }
        Map<String, FileViewResponse> views = new LinkedHashMap<>();
        for (String id : ids) {
            StoredFile storedFile = files.get(id);
            views.put(id, readyLogoView(storedFile));
        }
        return views;
    }

    private void validateId(String fileId) {
        try {
            if (fileId == null || !UUID.fromString(fileId).toString().equals(fileId)) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            log.warn("Invalid file upload ID", e);
            throw new AppException(ErrorCode.FILE_UPLOAD_NOT_FOUND);
        }
    }

    private StoredFile lockedUpload(UUID fileId) {
        if (fileId == null) {
            throw new AppException(ErrorCode.FILE_UPLOAD_NOT_FOUND);
        }
        return storedFileRepository.findForUpdate(fileId)
                .orElseThrow(() -> new AppException(ErrorCode.FILE_UPLOAD_NOT_FOUND));
    }

    private void finalizeUpload(StoredFile storedFile) {
        StatObjectResponse object = stat(storedFile.getBucket(), storedFile.getTemporaryObjectKey());
        if (!StringUtils.hasText(object.etag())) {
            throw new AppException(ErrorCode.FILE_STORAGE_ERROR);
        }

        try {
            if (!Objects.equals(storedFile.getContentType(), object.contentType())) {
                throw new AppException(ErrorCode.FILE_UPLOAD_TYPE_INVALID);
            }

            long maximum = fileUploadProperties.maxFileSizeForContentType(object.contentType());
            if (object.size() <= 0 || object.size() > maximum) {
                throw new AppException(ErrorCode.FILE_UPLOAD_SIZE_EXCEEDED);
            }

            if (!storedFile.getId().toString().equals(object.userMetadata().getFirst("file-id"))) {
                throw new AppException(ErrorCode.FILE_UPLOAD_REFERENCE_INVALID);
            }
        } catch (AppException e) {
            reject(storedFile);
            throw e;
        }

        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(storedFile.getBucket())
                    .object(storedFile.getObjectKey())
                    .source(SourceObject.builder()
                            .bucket(storedFile.getBucket())
                            .object(storedFile.getTemporaryObjectKey())
                            .versionId(object.versionId())
                            .matchETag(object.etag())
                            .build())
                    .build());
        } catch (Exception e) {
            throw storageFailure("Cannot finalize file upload", e);
        }

        StatObjectResponse finalized = stat(storedFile.getBucket(), storedFile.getObjectKey());
        if (finalized.size() != object.size()
                || !Objects.equals(finalized.contentType(), object.contentType())) {
            throw new AppException(ErrorCode.FILE_STORAGE_ERROR);
        }

        storedFile.setSize(finalized.size());
        storedFile.setContentType(finalized.contentType());
        storedFile.setStatus(StoredFileStatus.READY);
        storedFile.setReadyAt(Instant.now());
        storedFile.setLastModifiedBy(actor());
        storedFileRepository.saveAndFlush(storedFile);
        removeAfterCommit(storedFile.getBucket(), storedFile.getTemporaryObjectKey());
    }

    private void reject(StoredFile file) {
        file.setStatus(StoredFileStatus.FAILED);
        file.setLastModifiedBy(actor());
        storedFileRepository.saveAndFlush(file);
        removeAfterCommit(file.getBucket(), file.getObjectKey());
        removeAfterCommit(file.getBucket(), file.getTemporaryObjectKey());
    }

    private FileViewResponse readyLogoView(StoredFile storedFile) {
        if (storedFile.getDeletedAt() != null) {
            throw new AppException(ErrorCode.FILE_UPLOAD_NOT_FOUND);
        }
        if (storedFile.getStatus() != StoredFileStatus.READY
                || storedFile.getSize() == null
                || storedFile.getSize() <= 0
                || storedFile.getReadyAt() == null) {
            throw new AppException(ErrorCode.FILE_UPLOAD_NOT_READY);
        }
        if (!ImageFilePolicy.isAllowedContentType(storedFile.getContentType())) {
            throw new AppException(ErrorCode.FILE_UPLOAD_TYPE_INVALID);
        }
        return signedView(storedFile);
    }

    private FileViewResponse signedView(StoredFile file) {
        try {
            String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Http.Method.GET)
                    .bucket(file.getBucket())
                    .object(file.getObjectKey())
                    .expiry(fileUploadProperties.presignedGetUrlExpirySeconds(), TimeUnit.SECONDS)
                    .build());
            return new FileViewResponse(file.getId(), file.getFileName(), file.getContentType(),
                    file.getSize(), url, presignedUrlExpiresAt(url));
        } catch (Exception e) {
            throw storageFailure("Cannot create file view URL", e);
        }
    }

    private Instant presignedUrlExpiresAt(String url) {
        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUriString(url).build().getQueryParams();
        String signedAt = queryParams.getFirst("X-Amz-Date");
        String expirySeconds = queryParams.getFirst("X-Amz-Expires");
        if (signedAt == null || expirySeconds == null) {
            throw new AppException(ErrorCode.FILE_STORAGE_ERROR);
        }
        Instant signingTime = Instant.from(AMZ_DATE_FORMAT.parse(signedAt));
        return signingTime.plusSeconds(Long.parseLong(expirySeconds));
    }

    private StatObjectResponse stat(String bucket, String key) {
        try {
            return minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(key).build());
        } catch (ErrorResponseException e) {
            if (Set.of("NoSuchKey", "NoSuchObject", "NotFound").contains(e.errorResponse().code())) {
                throw new AppException(ErrorCode.FILE_UPLOAD_NOT_FOUND);
            }
            throw storageFailure("Cannot read uploaded file", e);
        } catch (Exception e) {
            throw storageFailure("Cannot read uploaded file", e);
        }
    }

    private AppException storageFailure(String message, Exception e) {
        log.error(message, e);
        return new AppException(ErrorCode.FILE_STORAGE_ERROR);
    }

    private void removeAfterCommit(String bucket, String key) {
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
            remove(bucket, key);
        } catch (Exception e) {
            log.warn("Cannot remove temporary file; cleanup will retry", e);
        }
    }

    private void remove(String bucket, String key) {
        if (!StringUtils.hasText(key)) return;
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(key).build());
        } catch (Exception e) {
            throw storageFailure("Cannot remove temporary file", e);
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
