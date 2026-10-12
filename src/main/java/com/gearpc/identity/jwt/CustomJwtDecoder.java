package com.gearpc.identity.jwt;

import com.gearpc.identity.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
public class CustomJwtDecoder implements JwtDecoder {

    private final NimbusJwtDecoder nimbusJwtDecoder;

    public CustomJwtDecoder(JwtProperties jwtProperties) {
        SecretKey secretKey = new SecretKeySpec(
                jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8),
                "HmacSHA512"
        );

        this.nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();

        nimbusJwtDecoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<Jwt>(
                        JwtValidators.createDefaultWithIssuer(jwtProperties.getIssuer()),
                        new JwtClaimValidator<Object>("exp", Objects::nonNull)
                )
        );
    }

    @Override
    public @NonNull Jwt decode(@NonNull String token) {
        return nimbusJwtDecoder.decode(token);
    }
}
