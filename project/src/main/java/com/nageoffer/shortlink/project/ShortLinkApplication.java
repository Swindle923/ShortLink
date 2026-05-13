package com.nageoffer.shortlink.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = {
        "com.nageoffer.shortlink.project.dao.mapper",
        "com.nageoffer.shortlink.project.admin.dao.mapper",
        "com.nageoffer.shortlink.project.console.dao.mapper"
})
public class ShortLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortLinkApplication.class, args);
    }
}
