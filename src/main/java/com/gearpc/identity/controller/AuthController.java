package com.gearpc.identity.controller;

import com.gearpc.common.dto.ApiResponse;
import com.gearpc.identity.application.dto.request.LoginRequest;
import com.gearpc.identity.application.dto.request.RegisterUserRequest;
import com.gearpc.identity.application.dto.response.LoginResponse;
import com.gearpc.identity.application.dto.response.RegisterUserResponse;
import com.gearpc.identity.application.service.AuthenticationService;
import com.gearpc.identity.application.service.UserService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

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

        LoginResponse responseData = getResponseData(response, data);

        return ApiResponse.success(responseData);
    }

    @PostMapping("/refresh-token")
    ApiResponse<LoginResponse> refreshToken(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) throws ParseException, JOSEException {
        var data = authenticationService.refreshToken(refreshToken);

        LoginResponse responseData = getResponseData(response, data);

        return ApiResponse.success(responseData);
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) throws ParseException, JOSEException {
        // 1. Gọi service để thu hồi token
        authenticationService.logout(refreshToken);

        // 2. Xóa refresh token cookie
        // Set value = "" và maxAge = 0 để browser xóa cookie
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setMaxAge(0); // Xóa cookie ngay lập tức

        response.addCookie(cookie);

        return ApiResponse.success();

    }

    @PostMapping("/logout-all")
    ApiResponse<Void> logoutAll(HttpServletResponse response) {
        authenticationService.logoutAll();

        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ApiResponse.success();
    }


    private static LoginResponse getResponseData(HttpServletResponse response, LoginResponse data) {
        // Lưu refresh token vào cookie
        Cookie cookie = new Cookie("refreshToken", data.refreshToken());
        cookie.setHttpOnly(true); // Ngăn chặn JavaScript từ việc truy cập Cookie (XSS protection)
        cookie.setSecure(false); // Đổi thành true khi triển khai trên production với HTTPS
        cookie.setPath("/"); // Cookie sẽ được gửi cho tất cả các endpoint trên domain
        cookie.setMaxAge(14 * 24 * 60 * 60); // Cookie expiry: 14 days — matches refresh token TTL
        response.addCookie(cookie);

        // Tạo response mới không chứa refresh token
        return LoginResponse.builder()
                .accessToken(data.accessToken())
                .refreshToken(null)
                .roles(data.roles())
                .build();
    }

}
