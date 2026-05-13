package com.nageoffer.shortlink.project.console.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.shortlink.project.common.database.BaseDO;
import lombok.Data;

@Data
@TableName("t_admin_audit_log")
public class AdminAuditLogDO extends BaseDO {

    private Long id;

    private String adminUsername;

    private String actionType;

    private String targetType;

    private String targetId;

    private String requestParams;

    private String ip;

    private Integer success;

    private String errorMessage;
}
