package com.gearpc.identity.config.security;

import com.gearpc.common.dto.CommonResultCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@AllArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    //private final HandlerExceptionResolver handlerExceptionResolver;
    private final SecurityErrorResponseWriter securityErrorResponseWriter;

    @Override
    public void commence(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AuthenticationException authException
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        //handlerExceptionResolver.resolveException(request, response, null, new AppException(ErrorCode.TOKEN_INVALID));
        securityErrorResponseWriter.write(
                request,
                response,
                CommonResultCodes.UNAUTHORIZED
        );

    }
}
