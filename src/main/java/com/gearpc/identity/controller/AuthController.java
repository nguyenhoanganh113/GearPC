package com.gearpc.identity.controller;

import com.gearpc.common.dto.ApiResponse;
import com.gearpc.identity.application.dto.request.RegisterUserRequest;
import com.gearpc.identity.application.dto.response.RegisterUserResponse;
import com.gearpc.identity.application.service.UserService;
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

    @PostMapping("/register")
    ApiResponse<RegisterUserResponse> registerUser(@RequestBody @Valid RegisterUserRequest registerUserRequest) {
        return ApiResponse.created(userService.registerUser(registerUserRequest));
    }

}
