package com.nageoffer.shortlink.project.console.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class ConsoleConfiguration {

    @Bean
    public FilterRegistrationBean<AdminRoleFilter> adminRoleFilter(StringRedisTemplate stringRedisTemplate) {
        FilterRegistrationBean<AdminRoleFilter> reg = new FilterRegistrationBean<>(new AdminRoleFilter(stringRedisTemplate));
        reg.addUrlPatterns("/api/short-link/console/v1/*");
        reg.setOrder(10);
        return reg;
    }
}
