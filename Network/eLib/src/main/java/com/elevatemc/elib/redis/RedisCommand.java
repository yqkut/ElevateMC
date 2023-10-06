package com.elevatemc.elib.redis;

import redis.clients.jedis.Jedis;

public interface RedisCommand<T> {

    T execute(Jedis redis);

}