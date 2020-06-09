package com.hupu.themis.admin.modules.monitor.service;

import redis.clients.jedis.Jedis;

public interface IRedisExecutor<T> {
    T execute(Jedis jedis);
}
