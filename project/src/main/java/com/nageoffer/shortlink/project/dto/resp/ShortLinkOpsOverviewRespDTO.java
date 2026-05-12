package com.nageoffer.shortlink.project.dto.resp;

import lombok.Data;

@Data
public class ShortLinkOpsOverviewRespDTO {

    private Integer linkCount;

    private Integer activeCount;

    private Integer expiringSoonCount;

    private Integer quotaRiskCount;

    private Integer exhaustedCount;

    private Integer highRiskCount;

    private Double healthyRate;

    private Long todayPv;

    private Long totalPv;
}
