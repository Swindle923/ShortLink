package com.nageoffer.shortlink.project.dto.resp;

import lombok.Data;

@Data
public class ShortLinkOpsLifecycleAlertRespDTO {

    private String fullShortUrl;

    private String describe;

    private String alertType;

    private String alertMessage;

    private Double quotaUsageRate;

    private Long daysToExpire;
}
