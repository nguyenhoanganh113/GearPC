package com.gearpc.identity.application.service.impl;

import com.gearpc.identity.application.service.RedisService;
import com.gearpc.identity.infrastructure.redis.RevokedTokenRedis;
import com.gearpc.identity.infrastructure.redis.RevokedTokenRedisRepository;
import com.gearpc.identity.infrastructure.redis.UserSessionRedis;
import com.gearpc.identity.infrastructure.redis.UserSessionRedisRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserSessionRedisRepository userSessionRedisRepository;
    private final RevokedTokenRedisRepository revokedTokenRedisRepository;

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setWithTTL(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
        //valueOperations.set(key, value, duration);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void saveUserSession(String jti, UUID userId, Instant expiresAt, long ttl) {
        UserSessionRedis userSessionRedis = UserSessionRedis.builder()
                .jti(jti)
                .userId(userId)
                .expiresAt(expiresAt)
                .ttl(ttl)
                .build();

        userSessionRedisRepository.save(userSessionRedis);
    }

    @Override
    public void saveRevokedToken(UUID userId, String jti, Instant expiresAt) {
        long ttl = Duration.between(Instant.now(), expiresAt).getSeconds();

        if (ttl <= 0) return;

        RevokedTokenRedis revokedTokenRedis = RevokedTokenRedis.builder()
                .jti(jti)
                .userId(userId)
                .expiresAt(expiresAt)
                .ttl(ttl)
                .build();

        revokedTokenRedisRepository.save(revokedTokenRedis);
    }

    @Override
    public boolean isRefreshTokenSessionActive(String jti) {
        return userSessionRedisRepository.existsById(jti);
    }

    @Override
    public void deleteAllUserSessions(UUID userId) {
        List<UserSessionRedis> sessions =
                userSessionRedisRepository.findAllByUserId(userId);

        userSessionRedisRepository.deleteAll(sessions);
    }

    @Override
    public boolean isTokenRevoked(String jti) {
        return revokedTokenRedisRepository.existsById(jti);
    }

    @Override
    public void deleteUserSession(String jti) {
        userSessionRedisRepository.deleteById(jti);
    }

    // === String operations với StringRedisTemplate (chuyên cho String) ===
    public void setString(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void setStringWithTTL(String key, String value, Duration duration) {
        stringRedisTemplate.opsForValue().set(key, value, duration);
    }

    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    // === Hash operations (lưu object) ===
    public void putHash(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Object getHash(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    // === List operations ===
    public void pushToList(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    public Object popFromList(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public Long getListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

}
