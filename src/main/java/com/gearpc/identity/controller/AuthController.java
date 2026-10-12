package com.gearpc.identity.controller;

import com.gearpc.common.dto.ApiResponse;
import com.gearpc.identity.application.dto.request.LoginRequest;
import com.gearpc.identity.application.dto.request.RegisterUserRequest;
import com.gearpc.identity.application.dto.response.LoginResponse;
import com.gearpc.identity.application.dto.response.RegisterUserResponse;
import com.gearpc.identity.application.service.AuthenticationService;
import com.gearpc.identity.application.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    ApiResponse<RegisterUserResponse> registerUser(@RequestBody @Valid RegisterUserRequest registerUserRequest) {
        return ApiResponse.created(userService.registerUser(registerUserRequest));
    }

    @PostMapping("/login")
    ApiResponse<LoginResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response
    ) {
        var data = authenticationService.login(request);

        // Lưu refresh token vào cookie
        Cookie cookie = new Cookie("refreshToken", data.refreshToken());
        cookie.setHttpOnly(true); // Ngăn chặn JavaScript từ việc truy cập Cookie (XSS protection)
        cookie.setSecure(false); // Đổi thành true khi triển khai trên production với HTTPS
        cookie.setPath("/"); // Cookie sẽ được gửi cho tất cả các endpoint trên domain
        cookie.setMaxAge(14 * 24 * 60 * 60); // Cookie expiry: 14 days — matches refresh token TTL
        response.addCookie(cookie);

        // Tạo response mới không chứa refresh token
        LoginResponse responseData = LoginResponse.builder()
                .accessToken(data.accessToken())
                .refreshToken(null) // Không trả về refresh token
                .roles(data.roles())
                .build();

        return ApiResponse.success(responseData);
    }

}
