package com.gearpc.identity.infrastructure.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserSessionRedisRepository extends CrudRepository<UserSessionRedis, String> {

    List<UserSessionRedis> findAllByUserId(UUID userId);

}
