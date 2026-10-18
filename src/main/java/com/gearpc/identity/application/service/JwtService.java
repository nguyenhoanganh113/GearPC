package com.gearpc.identity.application.service;

import com.gearpc.identity.application.dto.response.TokenDetails;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public interface JwtService {

    String generateAccessToken(UUID userId, Set<String> roles);
    TokenDetails generateRefreshToken(UUID userId);
    TokenDetails generateRefreshToken(UUID userId, Instant expiresAt);
    SignedJWT validateToken(String token) throws ParseException, JOSEException;
}
