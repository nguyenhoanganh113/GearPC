package com.gearpc.user.service;

import com.gearpc.user.dto.request.LoginRequest;
import com.gearpc.user.dto.response.LoginResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(String refreshToken) throws ParseException, JOSEException;

    void logout(String refreshToken) throws ParseException, JOSEException;

    void logoutAll();

}
