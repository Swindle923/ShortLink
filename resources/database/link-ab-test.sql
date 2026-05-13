-- A/B 测试模块：schema 变更（在 link.sql 主表后执行）

-- 1. t_link 增加 redirect_mode 区分普通/AB
ALTER TABLE `t_link` ADD COLUMN `redirect_mode` tinyint(1) DEFAULT 0 COMMENT '跳转模式 0:普通 1:A/B测试' AFTER `valid_date`;

-- 2. 访问日志记录本次命中的变体
ALTER TABLE `t_link_access_logs` ADD COLUMN `variant_key` varchar(8) DEFAULT NULL COMMENT 'A/B 变体标识 A/B/C/D' AFTER `locale`;
CREATE INDEX `idx_logs_fsu_variant` ON `t_link_access_logs` (`full_short_url`, `variant_key`);

-- 3. A/B 变体配置表
CREATE TABLE IF NOT EXISTS `t_link_ab_test`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `full_short_url` varchar(128)   NOT NULL COMMENT '完整短链接',
    `variant_key`    varchar(8)     NOT NULL COMMENT '变体标识 A/B/C/D',
    `target_url`     varchar(1024)  NOT NULL COMMENT '变体跳转目标',
    `weight`         int(11)        NOT NULL DEFAULT 50 COMMENT '权重 0-100',
    `hit_count`      int(11)        NOT NULL DEFAULT 0 COMMENT '命中次数',
    `create_time`    datetime       DEFAULT NULL,
    `update_time`    datetime       DEFAULT NULL,
    `del_flag`       tinyint(1)     DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_full_short_url` (`full_short_url`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接 A/B 测试变体配置';
