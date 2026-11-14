package com.gearpc.common.controller;

import com.gearpc.common.dto.ApiResponse;
import com.gearpc.common.dto.request.CreateFileUploadRequest;
import com.gearpc.common.dto.response.CreateFileUploadResponse;
import com.gearpc.common.service.FilePresignedUploadService;
import io.minio.errors.MinioException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FilePresignedUploadService filePresignedUploadService;

    @PostMapping(value = "/presigned-url", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<CreateFileUploadResponse> createPresignedUrl(
            @Valid @RequestBody CreateFileUploadRequest request
    ) throws MinioException {
        return ApiResponse.created(filePresignedUploadService.createPresignedUploadUrl(request));
    }


}
