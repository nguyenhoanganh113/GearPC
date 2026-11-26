package com.gearpc.user.controller;

import com.gearpc.user.common.dto.ApiResponse;
import com.gearpc.user.dto.UserDetailResponse;
import com.gearpc.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserDetailResponse> getMyProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ApiResponse.success(
                userService.getOrCreateProfile(jwt)
        );
    }


}
