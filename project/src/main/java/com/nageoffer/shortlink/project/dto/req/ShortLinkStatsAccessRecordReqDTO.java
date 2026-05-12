package com.nageoffer.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nageoffer.shortlink.project.dao.entity.LinkAccessLogsDO;
import lombok.Data;

@Data
public class ShortLinkStatsAccessRecordReqDTO extends Page<LinkAccessLogsDO> {

    private String fullShortUrl;

    private String gid;

    private String startDate;

    private String endDate;

    private Integer enableStatus;
}
