package com.gearpc.media.controller;

import com.gearpc.media.dto.ApiResponse;
import com.gearpc.media.dto.request.ConfirmFileUploadRequest;
import com.gearpc.media.dto.request.CreateFileUploadRequest;
import com.gearpc.media.dto.request.FilePresignedUrlsRequest;
import com.gearpc.media.dto.response.CreateFileUploadResponse;
import com.gearpc.media.dto.response.FileViewResponse;
import com.gearpc.media.service.FilePresignedUploadService;
import io.minio.errors.MinioException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class FileController {
    private final FilePresignedUploadService filePresignedUploadService;

    @PostMapping(value = "/presigned-url", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<CreateFileUploadResponse> createPresignedUrl(
            @Valid @RequestBody CreateFileUploadRequest request
    ) throws MinioException {
        return ApiResponse.created(filePresignedUploadService.createPresignedUploadUrl(request));
    }

    @PostMapping(value = "/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<FileViewResponse> confirmUpload(
            @Valid @RequestBody ConfirmFileUploadRequest request
    ) {
        return ApiResponse.success(filePresignedUploadService.confirmUpload(request));
    }

    @GetMapping("/{fileId}/presigned-url")
    public ApiResponse<FileViewResponse> getPresignedFileUrl(
            @PathVariable("fileId") String fileId
    ) {
        return ApiResponse.success(filePresignedUploadService.presignedGetInternal(fileId));
    }

    @PostMapping(value = "/presigned-urls", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Map<String, FileViewResponse>> getPresignedFileUrls(
            @Valid @RequestBody FilePresignedUrlsRequest request
    ) {
        return ApiResponse.success(filePresignedUploadService.presignedGetsInternal(request.fileIds()));
    }
}
