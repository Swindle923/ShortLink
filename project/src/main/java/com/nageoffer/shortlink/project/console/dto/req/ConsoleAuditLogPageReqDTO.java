package com.nageoffer.shortlink.project.console.dto.req;

import lombok.Data;

@Data
public class ConsoleAuditLogPageReqDTO {

    private String adminUsername;

    private String actionType;

    private String targetType;

    private Integer success;

    private Long current = 1L;

    private Long size = 20L;
}
