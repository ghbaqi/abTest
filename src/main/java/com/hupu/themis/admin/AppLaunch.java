package com.hupu.themis.admin;

import com.hupu.themis.admin.modules.common.utils.SpringContextHolder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan({"com.hupu.themis.admin.**.mapper"})
public class AppLaunch {

    public static void main(String[] args) {
        SpringApplication.run(AppLaunch.class, args);
    }

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }

}
