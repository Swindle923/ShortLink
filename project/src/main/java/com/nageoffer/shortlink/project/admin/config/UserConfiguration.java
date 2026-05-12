package com.nageoffer.shortlink.project.admin.config;

import com.nageoffer.shortlink.project.admin.common.biz.user.UserFlowRiskControlFilter;
import com.nageoffer.shortlink.project.admin.common.biz.user.UserAuthenticationFilter;
import com.nageoffer.shortlink.project.admin.common.biz.user.UserTransmitFilter;
import com.nageoffer.shortlink.project.common.monitoring.BizMetricsKit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class UserConfiguration {

    @Bean
    public FilterRegistrationBean<UserTransmitFilter> globalUserTransmitFilter() {
        FilterRegistrationBean<UserTransmitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserTransmitFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(0);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<UserAuthenticationFilter> userAuthenticationFilter(StringRedisTemplate stringRedisTemplate) {
        FilterRegistrationBean<UserAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserAuthenticationFilter(stringRedisTemplate));
        registration.addUrlPatterns("/*");
        registration.setOrder(5);
        return registration;
    }

    @Bean
    @ConditionalOnProperty(name = "short-link.flow-limit.enable", havingValue = "true")
    public FilterRegistrationBean<UserFlowRiskControlFilter> globalUserFlowRiskControlFilter(
            StringRedisTemplate stringRedisTemplate,
            UserFlowRiskControlConfiguration userFlowRiskControlConfiguration,
            BizMetricsKit bizMetricsKit) {
        FilterRegistrationBean<UserFlowRiskControlFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserFlowRiskControlFilter(stringRedisTemplate, userFlowRiskControlConfiguration, bizMetricsKit));
        registration.addUrlPatterns("/*");
        registration.setOrder(10);
        return registration;
    }
}
