package com.gearpc.media.service;

import com.gearpc.media.dto.request.CreateFileUploadRequest;
import com.gearpc.media.dto.request.ConfirmFileUploadRequest;
import com.gearpc.media.dto.response.CreateFileUploadResponse;
import com.gearpc.media.dto.response.FileViewResponse;
import io.minio.errors.MinioException;

import java.util.Collection;
import java.util.Map;

public interface FilePresignedUploadService {

    CreateFileUploadResponse createPresignedUploadUrl(CreateFileUploadRequest request) throws MinioException;

    FileViewResponse confirmUpload(ConfirmFileUploadRequest request);

    FileViewResponse presignedGetInternal(String fileId);

    Map<String, FileViewResponse> presignedGetsInternal(Collection<String> fileIds);

}
