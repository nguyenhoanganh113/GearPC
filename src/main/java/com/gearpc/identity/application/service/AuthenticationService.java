package com.gearpc.identity.application.service;

import com.gearpc.identity.application.dto.request.LoginRequest;
import com.gearpc.identity.application.dto.response.LoginResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

}
