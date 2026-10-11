package com.gearpc.identity.controller;

import com.gearpc.common.dto.ApiResponse;
import com.gearpc.identity.application.dto.request.LoginRequest;
import com.gearpc.identity.application.dto.request.RegisterUserRequest;
import com.gearpc.identity.application.dto.response.LoginResponse;
import com.gearpc.identity.application.dto.response.RegisterUserResponse;
import com.gearpc.identity.application.service.AuthenticationService;
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
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    ApiResponse<RegisterUserResponse> registerUser(@RequestBody @Valid RegisterUserRequest registerUserRequest) {
        return ApiResponse.created(userService.registerUser(registerUserRequest));
    }

    @PostMapping("/login")
    ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        var data = authenticationService.login(request);
        return ApiResponse.success(data);
    }

}
