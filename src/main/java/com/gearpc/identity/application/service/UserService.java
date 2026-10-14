package com.gearpc.identity.application.service;

import com.gearpc.identity.application.dto.UserDetailResponse;
import com.gearpc.identity.application.dto.request.RegisterUserRequest;
import com.gearpc.identity.application.dto.response.AdminUserResponse;
import com.gearpc.identity.application.dto.response.RegisterUserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest);

    UserDetailResponse getMyProfile(UUID userId);

    List<AdminUserResponse> getAllUser();

}
