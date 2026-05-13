-- 管理员中台扩展：在 link.sql 主表创建后执行
-- 1. 用户增加状态字段（默认 active；frozen=被冻结）
ALTER TABLE `t_user` ADD COLUMN `status` tinyint(1) DEFAULT 0 COMMENT '账户状态 0:正常 1:冻结' AFTER `role`;

-- 2. 管理员操作审计日志
CREATE TABLE IF NOT EXISTS `t_admin_audit_log`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `admin_username`  varchar(64)   DEFAULT NULL COMMENT '操作人',
    `action_type`     varchar(64)   DEFAULT NULL COMMENT '操作类型',
    `target_type`     varchar(32)   DEFAULT NULL COMMENT '对象类型 USER/LINK',
    `target_id`       varchar(256)  DEFAULT NULL COMMENT '对象标识',
    `request_params`  text COMMENT '请求参数 JSON',
    `ip`              varchar(64)   DEFAULT NULL COMMENT '操作 IP',
    `success`         tinyint(1)    DEFAULT 1 COMMENT '是否成功 1:成功 0:失败',
    `error_message`   varchar(1024) DEFAULT NULL COMMENT '错误信息',
    `create_time`     datetime      DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime      DEFAULT NULL COMMENT '更新时间',
    `del_flag`        tinyint(1)    DEFAULT 0 COMMENT '删除标识 0:未删除 1:已删除',
    PRIMARY KEY (`id`),
    KEY `idx_admin_username` (`admin_username`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员操作审计日志';

-- 3. 种子管理员（密码：admin123456，按现有明文存储策略）
INSERT INTO `t_user` (`username`, `password`, `real_name`, `phone`, `mail`, `role`, `status`, `create_time`, `update_time`, `del_flag`)
SELECT 'admin', 'admin123456', '系统管理员', '13800000000', 'admin@shortlink.local', 'ADMIN', 0, NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `t_user` WHERE `username` = 'admin');
