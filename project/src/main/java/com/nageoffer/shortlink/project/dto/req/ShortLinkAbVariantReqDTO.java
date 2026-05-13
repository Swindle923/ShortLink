package com.nageoffer.shortlink.project.dto.req;

import lombok.Data;

@Data
public class ShortLinkAbVariantReqDTO {

    private String variantKey;

    private String targetUrl;

    private Integer weight;
}
