package com.gearpc.identity.application.service;

import java.util.Set;
import java.util.UUID;

public interface JwtService {

    String generateAccessToken(UUID userId, Set<String> roles);
    String generateRefreshToken(UUID userId);

}
