package com.gearpc.user.service;

import com.gearpc.user.dto.UserDetailResponse;
import com.gearpc.user.dto.response.AdminUserResponse;

import java.util.List;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserService {

//    RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest);

    List<AdminUserResponse> getAllUser();

    UserDetailResponse getOrCreateProfile(Jwt jwt);

}
