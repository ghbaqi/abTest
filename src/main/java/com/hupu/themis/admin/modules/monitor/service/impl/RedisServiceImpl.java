package com.hupu.themis.admin.modules.monitor.service.impl;

import com.hupu.themis.admin.modules.common.utils.PageUtil;
import com.hupu.themis.admin.modules.monitor.domain.vo.RedisVo;
import com.hupu.themis.admin.modules.monitor.service.IRedisExecutor;
import com.hupu.themis.admin.modules.monitor.service.IRedisTransaction;
import com.hupu.themis.admin.modules.monitor.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @date 2019-12-10
 */
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    JedisPool pool;

    @Value("${spring.redis.database}")
    private Integer database;

    public <T> T execute(IRedisExecutor<T> executor) {
        Jedis jedis = pool.getResource();
        jedis.select(database);
        T result = null;
        try {
            result = executor.execute(jedis);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    @Override
    public Page findByKey(final String key, Pageable pageable) {
        return execute(jedis -> {
            List<RedisVo> redisVos = new ArrayList<>();
            String newKey = key;
            if (!key.equals("*")) {
                newKey = "*" + key + "*";
            }
            for (String s : jedis.keys(newKey)) {
                RedisVo redisVo = new RedisVo(s, jedis.get(s));
                redisVos.add(redisVo);
            }
            Page<RedisVo> page = new PageImpl<RedisVo>(
                    PageUtil.toPage(pageable.getPageNumber(), pageable.getPageSize(), redisVos),
                    pageable,
                    redisVos.size());
            return page;
        });
    }

    @Override
    public String set(RedisVo redisVo) {
        return execute(jedis -> jedis.set(redisVo.getKey(), redisVo.getValue()));
    }

    @Override
    public String set(String key, String value) {
        return execute(jedis -> jedis.set(key, value));
    }

    @Override
    public Long del(String key) {
        return execute(jedis -> jedis.del(key));
    }


    @Override
    public void flushdb() {
        execute(BinaryJedis::flushDB);
    }

    @Override
    public Long hset(String key, String field, String value) {
        return execute(jedis -> jedis.hset(key, field, value));
    }

    @Override
    public String hget(String key, String field) {
        return execute(jedis -> jedis.hget(key, field));
    }

    @Override
    public String hmset(String key, Map<String, String> hash) {
        return execute(jedis -> jedis.hmset(key, hash));
    }

    @Override
    public Long hdel(String key, String... fields) {
        return execute(jedis -> jedis.hdel(key, fields));
    }

    @Override
    public Boolean hexists(String key, String field) {
        return execute(jedis -> jedis.hexists(key, field));
    }

    @Override
    public Long sadd(String key, String... member) {
        return execute(jedis -> jedis.sadd(key, member));
    }

    public void delByPrefix(String prefix) {
        execute(jedis -> {
            String cursor = ScanParams.SCAN_POINTER_START;
            ScanParams scanParams = new ScanParams();
            scanParams.count(1000);
            scanParams.match(prefix + "*");
            ScanResult<String> scan = null;
            do {
                scan = jedis.scan(cursor, scanParams);
                Pipeline pipelined = jedis.pipelined();
                scan.getResult().forEach(key -> {
                    pipelined.del(key);
                });
                pipelined.sync();
                cursor = scan.getStringCursor();
            } while (!ScanParams.SCAN_POINTER_START.equals(scan.getStringCursor()));
            return null;
        });
    }

    @Override
    public void multi(IRedisTransaction transaction) {
        Jedis jedis = pool.getResource();
        jedis.select(database);
        Transaction multi = jedis.multi();
        try {
            transaction.multi(multi);
            multi.exec();
        } catch (Exception e) {
            e.printStackTrace();
            multi.discard();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

}
