package com.nageoffer.shortlink.aggregation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class MonolithCorsConfiguration implements WebMvcConfigurer {

    @Value("${short-link.cors.allowed-origins:http://localhost:5173,http://127.0.0.1:5173}")
    private String allowedOrigins;

    @Value("${short-link.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${short-link.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${short-link.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = splitCsv(allowedOrigins);
        String[] methods = splitCsv(allowedMethods);
        String[] headers = splitCsv(allowedHeaders);
        registry.addMapping("/**")
                .allowedOrigins(origins)
                .allowedMethods(methods)
                .allowedHeaders(headers)
                .allowCredentials(allowCredentials)
                .maxAge(3600);
    }

    private String[] splitCsv(String value) {
        if (!StringUtils.hasText(value)) {
            return new String[0];
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toArray(String[]::new);
    }
}
