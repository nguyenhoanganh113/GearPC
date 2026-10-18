package com.gearpc.identity.infrastructure.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RedisHash("revoked_token")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevokedTokenRedis {

    @Id
    private String jti;

    @Indexed
    private UUID userId;

    private Instant expiresAt;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long ttl;

}
