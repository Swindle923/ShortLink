package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.shortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@TableName("t_link_os_stats")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkOsStatsDO extends BaseDO {

    private Long id;

    private String fullShortUrl;

    private Date date;

    private Integer cnt;

    private String os;
}
