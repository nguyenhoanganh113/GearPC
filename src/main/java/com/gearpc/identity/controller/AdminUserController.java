package com.gearpc.identity.controller;

import com.gearpc.common.dto.ApiResponse;
import com.gearpc.identity.application.dto.response.AdminUserResponse;
import com.gearpc.identity.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<List<AdminUserResponse>> getAllUsers() {
        List<AdminUserResponse> responseList = userService.getAllUser();
        return ApiResponse.success(responseList);
    }

}
