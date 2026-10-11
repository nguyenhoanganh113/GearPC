package com.gearpc.identity.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class JwtProperties {

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.secret-key}")
    private String secretKey;

}
