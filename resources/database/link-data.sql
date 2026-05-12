INSERT INTO `t_group` (`id`, `gid`, `name`, `username`, `sort_order`, `create_time`, `update_time`, `del_flag`)
VALUES (1752265619253805057, 'tSUBMP', '默认分组', 'admin', 0, '2024-01-31 21:00:00', '2024-01-31 21:00:00', 0);

INSERT INTO `t_group_unique` (`id`, `gid`)
VALUES (1752265619253805057, 'tSUBMP');

INSERT INTO `t_user` (`id`, `username`, `password`, `real_name`, `phone`, `mail`, `deletion_time`, `create_time`, `update_time`, `del_flag`)
VALUES (1752265616481370113, 'admin', 'admin123456', 'admin', '13800000000', 'admin@example.com', NULL, '2024-01-31 21:00:00', '2024-01-31 21:00:00', 0);
