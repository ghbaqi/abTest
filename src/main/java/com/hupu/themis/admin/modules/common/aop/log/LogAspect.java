package com.hupu.themis.admin.modules.common.aop.log;

import cn.hutool.core.date.DateUtil;
import com.hupu.themis.admin.modules.common.utils.ThrowableUtil;
import com.hupu.themis.admin.modules.monitor.domain.Logging;
import com.hupu.themis.admin.modules.monitor.service.LoggingService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @date 2019-11-24
 */
@Component
@Aspect
@Slf4j
public class LogAspect {

    @Autowired
    private LoggingService loggingService;

    private long currentTime = 0L;

    /**
     * 配置切入点
     */
    @Pointcut("@annotation(com.hupu.themis.admin.modules.common.aop.log.Log)")
    public void logPointcut() {
        // 该方法无方法体,主要为了让同类中其他方法使用此切入点
    }

    /**
     * 配置环绕通知,使用在方法logPointcut()上注册的切入点
     *
     * @param joinPoint join point for advice
     */
    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result =joinPoint.proceed();
        currentTime = System.currentTimeMillis();
        Logging logging = new Logging("INFO",System.currentTimeMillis() - currentTime);
        logging.setCreateTime(DateUtil.date().toTimestamp());
        loggingService.save(joinPoint, logging);
        return result;
    }

    /**
     * 配置异常通知
     *
     * @param joinPoint join point for advice
     * @param e exception
     */
    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Logging logging = new Logging("ERROR",System.currentTimeMillis() - currentTime);
        logging.setExceptionDetail(ThrowableUtil.getStackTrace(e));
        logging.setCreateTime(DateUtil.date().toTimestamp());
        loggingService.save((ProceedingJoinPoint)joinPoint, logging);
    }
}
