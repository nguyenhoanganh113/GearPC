package com.gearpc.identity.application.service.impl;

import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import com.gearpc.identity.application.service.JwtService;
import com.gearpc.identity.config.JwtProperties;
import com.gearpc.identity.domain.valueobject.enums.TokenType;
import com.gearpc.identity.jwt.JwtClaims;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    public JwtServiceImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public String generateAccessToken(UUID userId, Set<String> roles) {
        Date issueTime = new Date();
        Date expiredTime = new Date(Instant.now().plus(2, ChronoUnit.HOURS).toEpochMilli());
        String jwtId = UUID.randomUUID().toString();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userId.toString())
                .issuer(jwtProperties.getIssuer())
                .claim(JwtClaims.ROLES, roles)
                .issueTime(issueTime)
                .expirationTime(expiredTime)
                .jwtID(jwtId)
                .claim(JwtClaims.TOKEN_TYPE, TokenType.ACCESS_TOKEN.name())
                .build();

        return sign(claimsSet);
    }

    @Override
    public String generateRefreshToken(UUID userId) {
        Date issueTime = new Date();
        Date expiredTime = new Date(Instant.now().plus(14, ChronoUnit.DAYS).toEpochMilli());
        String jwtId = UUID.randomUUID().toString();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userId.toString())
                .issuer(jwtProperties.getIssuer())
                .issueTime(issueTime)
                .expirationTime(expiredTime)
                .jwtID(jwtId)
                .claim(JwtClaims.TOKEN_TYPE, TokenType.REFRESH_TOKEN.name())
                .build();

        return sign(claimsSet);
    }

    private String sign(JWTClaimsSet claimsSet) {
        JWSObject jwsObject = new JWSObject(
                new JWSHeader(JWSAlgorithm.HS512),
                new Payload(claimsSet.toJSONObject())
        );

        try {
            jwsObject.sign(new MACSigner(jwtProperties.getSecretKey()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.TOKEN_GENERATION_FAILED);
        }
    }
}
