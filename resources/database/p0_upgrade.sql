-- P0 改造数据库升级脚本
-- 1) 访问次数限制字段
ALTER TABLE t_link
    ADD COLUMN IF NOT EXISTS max_access_count INT(11) NULL COMMENT '访问上限，为空表示不限制' AFTER total_uip,
    ADD COLUMN IF NOT EXISTS current_access_count INT(11) NOT NULL DEFAULT 0 COMMENT '当前访问次数' AFTER max_access_count;

-- 2) 角色字段
ALTER TABLE t_user
    ADD COLUMN IF NOT EXISTS role VARCHAR(32) NOT NULL DEFAULT 'USER' COMMENT '角色（ADMIN/USER）' AFTER mail;

-- 3) 兼容历史演示账号
UPDATE t_user SET role = 'ADMIN' WHERE username = 'admin';
