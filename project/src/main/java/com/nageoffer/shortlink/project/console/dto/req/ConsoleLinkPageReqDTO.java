package com.nageoffer.shortlink.project.console.dto.req;

import lombok.Data;

@Data
public class ConsoleLinkPageReqDTO {

    private String keyword;

    private String username;

    private Integer enableStatus;

    private Long current = 1L;

    private Long size = 10L;
}
