package com.hupu.themis.admin.modules.monitor.rest;

import com.hupu.themis.admin.modules.common.aop.limit.Limit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.concurrent.atomic.AtomicInteger;
@ApiIgnore
@RestController
@RequestMapping("test")
public class TestController {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();

    /**
     * 测试限流注解，下面配置说明该接口 60秒内最多只能访问 10次，保存到redis的键名为 limit_test，
     */
    @Limit(key = "test", period = 60, count = 10, name = "testLimit", prefix = "limit")
    @GetMapping("limit")
    public int testLimit() {
        return ATOMIC_INTEGER.incrementAndGet();
    }
}
