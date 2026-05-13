package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.shortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@TableName("t_link")
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkDO extends BaseDO {

    private Long id;

    private String domain;

    private String shortUri;

    private String fullShortUrl;

    private String originUrl;

    private Integer clickNum;

    private String gid;

    private Integer enableStatus;

    private Integer createdType;

    private Integer validDateType;

    private Date validDate;

    private Integer redirectMode;

    @TableField("`describe`")
    private String describe;

    private String favicon;

    private Integer totalPv;

    private Integer totalUv;

    private Integer totalUip;

    private Integer maxAccessCount;

    private Integer currentAccessCount;

    @TableField(exist = false)
    private Integer todayPv;

    @TableField(exist = false)
    private Integer todayUv;

    @TableField(exist = false)
    private Integer todayUip;

    private Long delTime;
}
