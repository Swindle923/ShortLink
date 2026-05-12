package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.shortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@TableName("t_link_locale_stats")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkLocaleStatsDO extends BaseDO {

    private Long id;

    private String fullShortUrl;

    private Date date;

    private Integer cnt;

    private String province;

    private String city;

    private String adcode;

    private String country;
}
