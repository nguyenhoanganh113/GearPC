package com.gearpc.user.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevokedTokenRedisRepository extends CrudRepository<RevokedTokenRedis, String> {
}
