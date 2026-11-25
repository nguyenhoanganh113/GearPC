package com.gearpc.common.service;

import com.gearpc.common.dto.request.CreateFileUploadRequest;
import com.gearpc.common.dto.request.ConfirmFileUploadRequest;
import com.gearpc.common.dto.response.CreateFileUploadResponse;
import com.gearpc.common.dto.response.FileViewResponse;
import io.minio.errors.MinioException;

import java.util.Collection;
import java.util.Map;

public interface FilePresignedUploadService {

    CreateFileUploadResponse createPresignedUploadUrl(CreateFileUploadRequest request) throws MinioException;

    FileViewResponse confirmUpload(ConfirmFileUploadRequest request);

    FileViewResponse presignedGetInternal(String fileId);

    Map<String, FileViewResponse> presignedGetsInternal(Collection<String> fileIds);

}
