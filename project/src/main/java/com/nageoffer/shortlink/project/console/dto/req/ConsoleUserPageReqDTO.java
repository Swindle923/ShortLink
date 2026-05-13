package com.nageoffer.shortlink.project.console.dto.req;

import lombok.Data;

@Data
public class ConsoleUserPageReqDTO {

    private String keyword;

    private String role;

    private Integer status;

    private Long current = 1L;

    private Long size = 10L;
}
