package com.hupu.themis.admin.modules.monitor.service;

import com.hupu.themis.admin.modules.monitor.domain.vo.RedisVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * 可自行扩展
 *
 * @date 2019-12-10
 */
public interface RedisService {

    /**
     * findById
     *
     * @param key
     * @return
     */
    public Page findByKey(String key, Pageable pageable);

    /**
     * create
     *
     * @param redisVo
     */
    public String set(RedisVo redisVo);

    /**
     * @param key
     * @param value
     * @return
     */
    public String set(String key, String value);

    /**
     * delete
     *
     * @param key
     */
    public Long del(String key);


    /**
     * 清空所有缓存
     */
    public void flushdb();

    Long hset(final String key, final String field, final String value);

    String hget(final String key, final String field);

    String hmset(final String key, final Map<String, String> hash);

    Long hdel(final String key, final String... fields);

    Boolean hexists(final String key, final String field);

    Long sadd(String key, String... member);

    void delByPrefix(String prefix);

    void multi(IRedisTransaction transaction);
}
