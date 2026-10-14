package com.gearpc.identity.application.service.impl;

import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import com.gearpc.identity.application.service.JwtService;
import com.gearpc.identity.config.security.JwtProperties;
import com.gearpc.identity.domain.valueobject.enums.TokenType;
import com.gearpc.identity.jwt.JwtClaims;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;
    private final JwtEncoder jwtEncoder;

    @Override
    public String generateAccessToken(UUID userId, Set<String> roles) {
        Instant now = Instant.now();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .subject(userId.toString())
                .issuer(jwtProperties.getIssuer())
                .claim(JwtClaims.ROLES, roles)
                .issuedAt(now)
                .expiresAt(now.plus(2, ChronoUnit.HOURS))
                .id(UUID.randomUUID().toString())
                .claim(JwtClaims.TOKEN_TYPE, TokenType.ACCESS_TOKEN.name())
                .build();

        return encode(claimsSet);
    }

    @Override
    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(userId.toString())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiresAt(now.plus(14, ChronoUnit.DAYS))
                .id(UUID.randomUUID().toString())
                .claim(JwtClaims.TOKEN_TYPE, TokenType.REFRESH_TOKEN.name())
                .build();

        return encode(claims);
    }

    @Override
    public SignedJWT validateToken(String token) throws ParseException, JOSEException {

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();

        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();

        if (expiration == null || jwtId == null) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        if(expiration.before(new Date()))
            throw new AppException(ErrorCode.TOKEN_EXPIRED);

        boolean verify = signedJWT.verify(new MACVerifier(jwtProperties.getSecretKey()));
        if(!verify)
            throw new AppException(ErrorCode.TOKEN_INVALID);

        return signedJWT;
    }

    private String encode(JwtClaimsSet claims) {
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS512).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }
}
