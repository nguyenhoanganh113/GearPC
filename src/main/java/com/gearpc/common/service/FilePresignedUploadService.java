package com.gearpc.common.service;

import com.gearpc.common.dto.request.CreateFileUploadRequest;
import com.gearpc.common.dto.response.CreateFileUploadResponse;
import io.minio.errors.MinioException;

public interface FilePresignedUploadService {

    CreateFileUploadResponse createPresignedUploadUrl(CreateFileUploadRequest request) throws MinioException;

}
