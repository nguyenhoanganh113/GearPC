package com.gearpc.identity.application.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public interface RedisService {

    void set(String key, Object value);

    void setWithTTL(String key, Object value, Duration duration);

    Object get(String key);

    boolean delete(String key);

    void setString(String key, String value);

    void setStringWithTTL(String key, String value, Duration duration);

    String getString(String key);

    void putHash(String key, String hashKey, Object value);

    Object getHash(String key, String hashKey);

    void pushToList(String key, Object value);

    Object popFromList(String key);

    Long getListSize(String key);

    boolean hasKey(String key);

    void saveUserSession(
            String jti,
            UUID userId,
            Instant expiresAt,
            long ttl
    );

    void saveRevokedToken(
            UUID userId,
            String jti,
            Instant expiresAt
    );

    boolean isRefreshTokenSessionActive(String jti);

    void deleteAllUserSessions(UUID userId);

    boolean isTokenRevoked(String jti);

    void deleteUserSession(String jti);

}
