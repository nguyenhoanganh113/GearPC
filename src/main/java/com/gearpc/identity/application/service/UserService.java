package com.gearpc.identity.application.service;

import com.gearpc.identity.application.dto.UserDetailResponse;
import com.gearpc.identity.application.dto.response.AdminUserResponse;

import java.util.List;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserService {

//    RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest);

    List<AdminUserResponse> getAllUser();

    UserDetailResponse getOrCreateProfile(Jwt jwt);

}
