package com.hupu.themis.admin.modules.monitor.service;

import redis.clients.jedis.Transaction;

public interface IRedisTransaction {
    void multi(Transaction multi);
}
