package com.gearpc.common.dto;

import lombok.Builder;

import java.util.Collections;
import java.util.List;

@Builder
public record PaginationResponse<T>(
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        List<T> content
) {
    public PaginationResponse {
        if (content == null) {
            content = Collections.emptyList();
        }
    }

}
