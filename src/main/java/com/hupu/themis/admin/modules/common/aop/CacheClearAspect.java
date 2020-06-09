package com.hupu.themis.admin.modules.common.aop;

import cn.hutool.core.annotation.AnnotationUtil;
import com.hupu.themis.admin.modules.monitor.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @date 2019-11-24
 */
@Component
@Aspect
@Slf4j
public class CacheClearAspect {

    @Autowired
    private RedisService redisService;

    /**
     * 配置切入点
     */
    @Pointcut("@annotation(com.hupu.themis.admin.modules.common.aop.CacheClear)")
    public void logPointcut() {
        // 该方法无方法体,主要为了让同类中其他方法使用此切入点
    }

    @After("logPointcut()")
    public void logAfter(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String cacheNames = AnnotationUtil.getAnnotationValue(method, CacheClear.class, "cacheNames");
        String[] cacheNameArr = cacheNames.split(",");
        for (String name : cacheNameArr) {
            redisService.delByPrefix(name + "::");
        }
    }

}
