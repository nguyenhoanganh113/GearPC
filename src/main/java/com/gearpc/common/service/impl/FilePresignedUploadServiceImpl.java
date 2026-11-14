package com.gearpc.common.service.impl;

import com.gearpc.common.config.minio.FileUploadProperties;
import com.gearpc.common.config.minio.MinioProperties;
import com.gearpc.common.dto.request.CreateFileUploadRequest;
import com.gearpc.common.dto.response.CreateFileUploadResponse;
import com.gearpc.common.entity.StoredFile;
import com.gearpc.common.enums.StoredFileStatus;
import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import com.gearpc.common.repository.StoredFileRepository;
import com.gearpc.common.service.FilePresignedUploadService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
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

    // chuyển đổi phần sau dấu . thành lower case
    private String extension(String name) {
        int dot = name.lastIndexOf('.');
        return dot < 0 ? "" : name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private String actor() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? "SYSTEM" : authentication.getName();
    }
}
