package com.gearpc.identity.application.service;

import com.gearpc.identity.application.dto.UserDetailResponse;
import com.gearpc.identity.application.dto.request.RegisterUserRequest;
import com.gearpc.identity.application.dto.response.AdminUserResponse;
import com.gearpc.identity.application.dto.response.RegisterUserResponse;

import java.util.List;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.Authentication;

public interface UserService {

//    RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest);

    UserDetailResponse getMyProfile(UUID userId);

    List<AdminUserResponse> getAllUser();

    UserDetailResponse getMyProfileByKeycloakId(UUID keycloakId);

    UserDetailResponse getOrCreateProfile(Jwt jwt, Authentication authentication);

}
