package com.gearpc.identity.application.service;

import com.gearpc.identity.application.dto.request.RegisterUserRequest;
import com.gearpc.identity.application.dto.response.RegisterUserResponse;

public interface UserService {

    RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest);

}
