package com.nageoffer.shortlink.project.admin.dto.resp;

import lombok.Data;

@Data
public class ShortLinkGroupRespDTO {

    private String gid;

    private String name;

    private Integer sortOrder;

    private Integer shortLinkCount;
}
