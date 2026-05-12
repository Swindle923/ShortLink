package com.nageoffer.shortlink.admin.remote.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ShortLinkPageRespDTO {

    private Long id;

    private String domain;

    private String shortUri;

    private String fullShortUrl;

    private String originUrl;

    private String gid;

    private Integer validDateType;

    private Integer enableStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String describe;

    private String favicon;

    private Integer totalPv;

    private Integer todayPv;

    private Integer totalUv;

    private Integer todayUv;

    private Integer totalUip;

    private Integer todayUip;

    private Integer maxAccessCount;

    private Integer currentAccessCount;
}
