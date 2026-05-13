-- 管理员中台扩展：在 link.sql 主表创建后执行
-- 1. 用户增加状态字段（默认 active；frozen=被冻结）
ALTER TABLE `t_user` ADD COLUMN `status` tinyint(1) DEFAULT 0 COMMENT '账户状态 0:正常 1:冻结' AFTER `role`;

-- 2. 种子管理员（密码：admin123456，按现有明文存储策略）
INSERT INTO `t_user` (`username`, `password`, `real_name`, `phone`, `mail`, `role`, `status`, `create_time`, `update_time`, `del_flag`)
SELECT 'admin', 'admin123456', '系统管理员', '13800000000', 'admin@shortlink.local', 'ADMIN', 0, NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `t_user` WHERE `username` = 'admin');
