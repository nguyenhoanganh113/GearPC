package com.gearpc.identity.config.security;

import com.gearpc.common.dto.CommonResultCodes;
import com.gearpc.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

@AllArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final JsonMapper jsonMapper;

    @Override
    public void handle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AccessDeniedException accessDeniedException
    ) throws IOException {
        // 1. Lấy FORBIDDEN trong CommonResultCodes
        CommonResultCodes forbidden = CommonResultCodes.FORBIDDEN;

        // 2. Lấy code
        String code = forbidden.getCode();

        // 3. Lấy message
        String message = forbidden.getMessage();

        // 4. Set content type là JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 5. Tạo ErrorResponse object
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(System.currentTimeMillis())
                .code(code)
                .errorName(forbidden.name())
                .message(message)
                .path(request.getRequestURI())
                .build();

        // 6. Convert ErrorResponse → JSON và write vào response
        response.getWriter().write(jsonMapper.writeValueAsString(errorResponse));

        // 7. Flush buffer để gửi response ngay lập tức
        response.flushBuffer();
    }
}
