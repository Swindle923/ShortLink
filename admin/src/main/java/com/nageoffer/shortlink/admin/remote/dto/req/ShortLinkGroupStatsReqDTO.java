package com.nageoffer.shortlink.admin.remote.dto.req;

import lombok.Data;

@Data
public class ShortLinkGroupStatsReqDTO {

    private String gid;

    private String startDate;

    private String endDate;
}
