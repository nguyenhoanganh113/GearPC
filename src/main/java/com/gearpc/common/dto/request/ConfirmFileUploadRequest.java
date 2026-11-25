package com.gearpc.common.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ConfirmFileUploadRequest(@NotNull UUID fileId) {}
