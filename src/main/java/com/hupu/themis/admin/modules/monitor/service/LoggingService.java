package com.hupu.themis.admin.modules.monitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hupu.themis.admin.modules.monitor.domain.Logging;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.scheduling.annotation.Async;

/**
 *
 * @date 2019-11-24
 */
public interface LoggingService extends IService<Logging> {

    /**
     * 新增日志
     * @param joinPoint
     * @param logging
     */
    @Async
    void save(ProceedingJoinPoint joinPoint, Logging logging);
}
