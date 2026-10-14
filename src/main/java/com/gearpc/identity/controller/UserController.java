package com.gearpc.identity.controller;

import com.gearpc.common.dto.ApiResponse;
import com.gearpc.identity.application.dto.UserDetailResponse;
import com.gearpc.identity.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserDetailResponse> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.success(
                userService.getMyProfile(UUID.fromString(Objects.requireNonNull(jwt.getSubject())))
        );
    }


}
