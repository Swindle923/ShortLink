package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.shortlink.project.common.database.BaseDO;
import lombok.Data;

@Data
@TableName("t_link_ab_test")
public class LinkAbTestDO extends BaseDO {

    private Long id;

    private String fullShortUrl;

    private String variantKey;

    private String targetUrl;

    private Integer weight;

    private Integer hitCount;
}
