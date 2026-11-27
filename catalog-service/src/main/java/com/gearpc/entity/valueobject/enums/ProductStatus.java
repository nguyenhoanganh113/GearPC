package com.gearpc.entity.valueobject.enums;

import com.gearpc.exception.AppException;
import com.gearpc.exception.ErrorCode;

public enum ProductStatus {
    ACTIVE,
    INACTIVE;

    public static ProductStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return ProductStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_PRODUCT_STATUS);
        }
    }
}
