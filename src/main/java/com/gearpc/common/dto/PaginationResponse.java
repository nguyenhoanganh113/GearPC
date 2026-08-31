package com.gearpc.common.dto;

import java.util.List;

public record PaginationResponse<T>(
        List<T> content,
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

}
