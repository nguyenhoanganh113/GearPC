package com.gearpc.identity.application.service;

import com.gearpc.identity.application.dto.request.LoginRequest;
import com.gearpc.identity.application.dto.response.LoginResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(String refreshToken) throws ParseException, JOSEException;

    void logout(String refreshToken) throws ParseException, JOSEException;

}
