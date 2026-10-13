package com.gearpc.identity.jwt;

import com.gearpc.identity.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
public class CustomJwtEncoder implements JwtEncoder {

    private final JwtProperties jwtProperties;

    @Override
    public @NonNull Jwt encode(@NonNull JwtEncoderParameters parameters) throws JwtEncodingException {
        SecretKey secretKey = new SecretKeySpec(
                jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8),
                "HmacSHA512"
        );

        NimbusJwtEncoder nimbusJwtEncoder = NimbusJwtEncoder
                .withSecretKey(secretKey)
                .algorithm(MacAlgorithm.HS512)
                .build();

        return nimbusJwtEncoder.encode(parameters);
    }
}
