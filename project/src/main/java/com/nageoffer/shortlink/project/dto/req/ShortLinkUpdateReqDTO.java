package com.nageoffer.shortlink.project.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ShortLinkUpdateReqDTO {

    private String originUrl;

    private String fullShortUrl;

    private String originGid;

    private String gid;

    private Integer validDateType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validDate;

    private String describe;

    private Integer maxAccessCount;
}
