package com.gearpc.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record ApiResponse<T>(
        String code,
        String message,
        T data
) {
    public static <T> ApiResponse<T> ok(String code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    public static <T> ApiResponse<T> created(String code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    public static <T> ApiResponse<T> noContent(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> success(T data){
        return ApiResponse.<T>builder()
                .code(CommonResultCodes.SUCCESS.getCode())
                .message(CommonResultCodes.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    public static ApiResponse<Void> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return error(code, message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return error(CommonResultCodes.INTERNAL_SERVER_ERROR.getCode(), message);
    }

    public static <T> ApiResponse<Void> error() {
        return error(CommonResultCodes.INTERNAL_SERVER_ERROR);
    }

    public static <T> ApiResponse<T> error(String code, String message, T data) {
        return ApiResponse.<T>builder()
                .code(CommonResultCodes.INTERNAL_SERVER_ERROR.getCode())
                .message(CommonResultCodes.INTERNAL_SERVER_ERROR.getMessage())
                .build();
    }

    public static <T> ApiResponse<T> error(ResultCode resultCode) {
        return error(resultCode.getCode(), resultCode.getMessage());
    }

    public static <T> ApiResponse<T> result(ResultCode resultCode) {
        return ApiResponse.<T>builder()
                .code(resultCode.getCode())
                .message(resultCode.getMessage())
                .data(null)
                .build();
    }

    public static <T> ApiResponse<T> result(ApiResponse<T> result) {
        return result(result.code, result.message, result.data);
    }

    public static <T> ApiResponse<T> result(String code, String message, T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponse<Void> judge(boolean status) {
        return status ? success() : error();
    }

    public static boolean isSuccess(String code) {
        return CommonResultCodes.SUCCESS.getCode().equals(code);
    }

}
