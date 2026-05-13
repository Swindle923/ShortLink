package com.nageoffer.shortlink.project.console.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ConsoleLinkRespDTO {

    private Long id;

    private String fullShortUrl;

    private String originUrl;

    private String gid;

    private String ownerUsername;

    private Integer enableStatus;

    private Integer validDateType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validDate;

    private Integer totalPv;

    private Integer totalUv;

    private Integer totalUip;

    private String describe;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
