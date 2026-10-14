package com.gearpc.identity.config.security;

import com.gearpc.common.dto.CommonResultCodes;
import com.gearpc.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

@AllArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    //private final HandlerExceptionResolver handlerExceptionResolver;
    private final JsonMapper jsonMapper; // dùng để convert Java object -> JSON string

    @Override
    public void commence(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AuthenticationException authException
    ) throws IOException {

        //handlerExceptionResolver.resolveException(request, response, null, new AppException(ErrorCode.TOKEN_INVALID));

        // 1. Lấy UNAUTHORIZED trong CommonResultCodes
        CommonResultCodes unauthorized = CommonResultCodes.UNAUTHORIZED;

        // 2. Lấy code
        String code = unauthorized.getCode();

        // 3. Lấy message
        String message = unauthorized.getMessage();

        // 4. Set content type là JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 5. Tạo ErrorResponse object
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(System.currentTimeMillis())
                .code(code)
                .errorName(unauthorized.name())
                .message(message)
                .path(request.getRequestURI())
                .build();

        // 6. Convert ErrorResponse → JSON và write vào response
        response.getWriter().write(jsonMapper.writeValueAsString(errorResponse));

        // 7. Flush buffer để gửi response ngay lập tức
        response.flushBuffer();
    }
}
