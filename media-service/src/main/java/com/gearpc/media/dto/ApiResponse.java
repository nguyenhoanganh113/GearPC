package com.gearpc.media.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(String code, String message, T data) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("200", "Thành công", data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>("201", "Tạo mới thành công", data);
    }
}
