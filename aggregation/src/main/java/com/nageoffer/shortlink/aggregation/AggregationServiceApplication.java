package com.nageoffer.shortlink.aggregation;

import com.nageoffer.shortlink.admin.ShortLinkAdminApplication;
import com.nageoffer.shortlink.project.ShortLinkApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication(scanBasePackages = {
        "com.nageoffer.shortlink.admin",
        "com.nageoffer.shortlink.project",
        "com.nageoffer.shortlink.aggregation"
})
@ComponentScan(
        basePackages = {
                "com.nageoffer.shortlink.admin",
                "com.nageoffer.shortlink.project",
                "com.nageoffer.shortlink.aggregation"
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ShortLinkAdminApplication.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ShortLinkApplication.class)
        }
)
@MapperScan(value = {
        "com.nageoffer.shortlink.project.dao.mapper",
        "com.nageoffer.shortlink.admin.dao.mapper"
})
public class AggregationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AggregationServiceApplication.class, args);
    }
}
