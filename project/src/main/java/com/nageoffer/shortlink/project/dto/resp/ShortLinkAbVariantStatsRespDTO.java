package com.nageoffer.shortlink.project.dto.resp;

import lombok.Data;

@Data
public class ShortLinkAbVariantStatsRespDTO {

    private String variantKey;

    private String targetUrl;

    private Integer weight;

    private Integer hitCount;

    private Integer pv;

    private Integer uv;

    private Integer uip;

    private Double pvRatio;
}
