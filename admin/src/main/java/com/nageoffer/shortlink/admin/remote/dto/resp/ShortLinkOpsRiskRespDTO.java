package com.nageoffer.shortlink.admin.remote.dto.resp;

import lombok.Data;

@Data
public class ShortLinkOpsRiskRespDTO {

    private String fullShortUrl;

    private String describe;

    private Integer riskScore;

    private String riskLevel;

    private String riskReason;

    private Integer todayPv;

    private Integer totalPv;

    private Double quotaUsageRate;

    private Long daysToExpire;

    private Double visitSpikeRatio;
}
