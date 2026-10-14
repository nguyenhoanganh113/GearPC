package com.gearpc.identity.config.security;

import com.gearpc.common.dto.CommonResultCodes;
import com.gearpc.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@Component
public class SecurityErrorResponseWriter {

    private final JsonMapper jsonMapper; // dùng để convert Java object -> JSON string

    public void write (
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            CommonResultCodes commonResultCodes
    ) throws IOException {

        // 1. Lấy code
        String code = commonResultCodes.getCode();

        // 2. Lấy message
        String message = commonResultCodes.getMessage();

        // 3. Set content type là JSON và character encoding
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 4. Tạo ErrorResponse object
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(System.currentTimeMillis())
                .code(code)
                .errorName(commonResultCodes.name())
                .message(message)
                .path(httpServletRequest.getRequestURI())
                .build();

        // 5. Convert ErrorResponse → JSON và write vào response
        httpServletResponse.getWriter().write(jsonMapper.writeValueAsString(errorResponse));

        // 6. Flush buffer để gửi response ngay lập tức
        httpServletResponse.flushBuffer();
    }

}
