package com.hupu.themis.admin.modules.quartz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hupu.themis.admin.modules.quartz.domain.QuartzJob;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * @author 郑杰
 * @date 2018/10/05 19:17:38
 */
@CacheConfig(cacheNames = "quartzJob")
public interface QuartzJobService extends IService<QuartzJob> {

    /**
     * create
     * @param resources
     * @return
     */
    @CacheEvict(allEntries = true)
    QuartzJob create(QuartzJob resources);

    /**
     * update
     * @param resources
     * @return
     */
    @CacheEvict(allEntries = true)
    void update(QuartzJob resources);

    /**
     * del
     * @param quartzJob
     */
    @CacheEvict(allEntries = true)
    void delete(QuartzJob quartzJob);

    /**
     * findById
     * @param id
     * @return
     */
    @Cacheable(key = "#p0")
    QuartzJob findById(Long id);

    /**
     * 更改定时任务状态
     * @param quartzJob
     */
    @CacheEvict(allEntries = true)
    void updateIsPause(QuartzJob quartzJob);

    /**
     * 立即执行定时任务
     * @param quartzJob
     */
    void execution(QuartzJob quartzJob);

    /**
     * 分页查询
     * @param resources
     * @param pageable
     * @return
     */
    @Cacheable(keyGenerator = "keyGenerator")
    Map queryAll(QuartzJob resources, Pageable pageable);
}
