package com.nageoffer.shortlink.aggregation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "short-link.demo-mode")
public class DemoModeProperties {

    private Boolean enable;

    private List<String> blacklist;
}
