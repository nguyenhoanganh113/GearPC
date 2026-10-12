package com.gearpc.identity.application.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;
import java.util.Set;
import java.util.UUID;

public interface JwtService {

    String generateAccessToken(UUID userId, Set<String> roles);
    String generateRefreshToken(UUID userId);
    SignedJWT validateToken(String token) throws ParseException, JOSEException;
}
