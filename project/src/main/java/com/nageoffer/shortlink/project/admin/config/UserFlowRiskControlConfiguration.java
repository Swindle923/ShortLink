package com.nageoffer.shortlink.project.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "short-link.flow-limit")
public class UserFlowRiskControlConfiguration {

    private Boolean enable;

    private String timeWindow;

    private Long maxAccessCount;

    private List<String> excludeUris = new ArrayList<>();

    private List<String> exemptUsers = new ArrayList<>();

    private String rejectMessage = "当前系统繁忙，请稍后再试";
}
