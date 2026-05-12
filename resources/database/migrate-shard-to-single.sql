-- 执行前请先备份数据库
-- 该脚本用于把历史分表数据合并到单表

INSERT INTO t_user (id, username, password, real_name, phone, mail, role, deletion_time, create_time, update_time, del_flag)
SELECT id, username, password, real_name, phone, mail, 'USER', deletion_time, create_time, update_time, del_flag FROM t_user_0
UNION ALL SELECT id, username, password, real_name, phone, mail, 'USER', deletion_time, create_time, update_time, del_flag FROM t_user_1
UNION ALL SELECT id, username, password, real_name, phone, mail, 'USER', deletion_time, create_time, update_time, del_flag FROM t_user_2
UNION ALL SELECT id, username, password, real_name, phone, mail, 'USER', deletion_time, create_time, update_time, del_flag FROM t_user_3
UNION ALL SELECT id, username, password, real_name, phone, mail, 'USER', deletion_time, create_time, update_time, del_flag FROM t_user_4
UNION ALL SELECT id, username, password, real_name, phone, mail, 'USER', deletion_time, create_time, update_time, del_flag FROM t_user_5
UNION ALL SELECT id, username, password, real_name, phone, mail, 'USER', deletion_time, create_time, update_time, del_flag FROM t_user_6
UNION ALL SELECT id, username, password, real_name, phone, mail, 'USER', deletion_time, create_time, update_time, del_flag FROM t_user_7
UNION ALL SELECT id, username, password, real_name, phone, mail, 'USER', deletion_time, create_time, update_time, del_flag FROM t_user_8
UNION ALL SELECT id, username, password, real_name, phone, mail, 'USER', deletion_time, create_time, update_time, del_flag FROM t_user_9
UNION ALL SELECT id, username, password, real_name, phone, mail, 'USER', deletion_time, create_time, update_time, del_flag FROM t_user_10
UNION ALL SELECT id, username, password, real_name, phone, mail, 'USER', deletion_time, create_time, update_time, del_flag FROM t_user_11
UNION ALL SELECT id, username, password, real_name, phone, mail, 'USER', deletion_time, create_time, update_time, del_flag FROM t_user_12
UNION ALL SELECT id, username, password, real_name, phone, mail, 'USER', deletion_time, create_time, update_time, del_flag FROM t_user_13
UNION ALL SELECT id, username, password, real_name, phone, mail, 'USER', deletion_time, create_time, update_time, del_flag FROM t_user_14
UNION ALL SELECT id, username, password, real_name, phone, mail, 'USER', deletion_time, create_time, update_time, del_flag FROM t_user_15;

INSERT INTO t_group (id, gid, name, username, sort_order, create_time, update_time, del_flag)
SELECT id, gid, name, username, sort_order, create_time, update_time, del_flag FROM t_group_0
UNION ALL SELECT id, gid, name, username, sort_order, create_time, update_time, del_flag FROM t_group_1
UNION ALL SELECT id, gid, name, username, sort_order, create_time, update_time, del_flag FROM t_group_2
UNION ALL SELECT id, gid, name, username, sort_order, create_time, update_time, del_flag FROM t_group_3
UNION ALL SELECT id, gid, name, username, sort_order, create_time, update_time, del_flag FROM t_group_4
UNION ALL SELECT id, gid, name, username, sort_order, create_time, update_time, del_flag FROM t_group_5
UNION ALL SELECT id, gid, name, username, sort_order, create_time, update_time, del_flag FROM t_group_6
UNION ALL SELECT id, gid, name, username, sort_order, create_time, update_time, del_flag FROM t_group_7
UNION ALL SELECT id, gid, name, username, sort_order, create_time, update_time, del_flag FROM t_group_8
UNION ALL SELECT id, gid, name, username, sort_order, create_time, update_time, del_flag FROM t_group_9
UNION ALL SELECT id, gid, name, username, sort_order, create_time, update_time, del_flag FROM t_group_10
UNION ALL SELECT id, gid, name, username, sort_order, create_time, update_time, del_flag FROM t_group_11
UNION ALL SELECT id, gid, name, username, sort_order, create_time, update_time, del_flag FROM t_group_12
UNION ALL SELECT id, gid, name, username, sort_order, create_time, update_time, del_flag FROM t_group_13
UNION ALL SELECT id, gid, name, username, sort_order, create_time, update_time, del_flag FROM t_group_14
UNION ALL SELECT id, gid, name, username, sort_order, create_time, update_time, del_flag FROM t_group_15;

INSERT INTO t_link (id, domain, short_uri, full_short_url, origin_url, click_num, gid, favicon, enable_status, created_type, valid_date_type, valid_date, `describe`, total_pv, total_uv, total_uip, max_access_count, current_access_count, create_time, update_time, del_time, del_flag)
SELECT id, domain, short_uri, full_short_url, origin_url, click_num, gid, favicon, enable_status, created_type, valid_date_type, valid_date, `describe`, total_pv, total_uv, total_uip, NULL, 0, create_time, update_time, del_time, del_flag FROM t_link_0
UNION ALL SELECT id, domain, short_uri, full_short_url, origin_url, click_num, gid, favicon, enable_status, created_type, valid_date_type, valid_date, `describe`, total_pv, total_uv, total_uip, NULL, 0, create_time, update_time, del_time, del_flag FROM t_link_1
UNION ALL SELECT id, domain, short_uri, full_short_url, origin_url, click_num, gid, favicon, enable_status, created_type, valid_date_type, valid_date, `describe`, total_pv, total_uv, total_uip, NULL, 0, create_time, update_time, del_time, del_flag FROM t_link_2
UNION ALL SELECT id, domain, short_uri, full_short_url, origin_url, click_num, gid, favicon, enable_status, created_type, valid_date_type, valid_date, `describe`, total_pv, total_uv, total_uip, NULL, 0, create_time, update_time, del_time, del_flag FROM t_link_3
UNION ALL SELECT id, domain, short_uri, full_short_url, origin_url, click_num, gid, favicon, enable_status, created_type, valid_date_type, valid_date, `describe`, total_pv, total_uv, total_uip, NULL, 0, create_time, update_time, del_time, del_flag FROM t_link_4
UNION ALL SELECT id, domain, short_uri, full_short_url, origin_url, click_num, gid, favicon, enable_status, created_type, valid_date_type, valid_date, `describe`, total_pv, total_uv, total_uip, NULL, 0, create_time, update_time, del_time, del_flag FROM t_link_5
UNION ALL SELECT id, domain, short_uri, full_short_url, origin_url, click_num, gid, favicon, enable_status, created_type, valid_date_type, valid_date, `describe`, total_pv, total_uv, total_uip, NULL, 0, create_time, update_time, del_time, del_flag FROM t_link_6
UNION ALL SELECT id, domain, short_uri, full_short_url, origin_url, click_num, gid, favicon, enable_status, created_type, valid_date_type, valid_date, `describe`, total_pv, total_uv, total_uip, NULL, 0, create_time, update_time, del_time, del_flag FROM t_link_7
UNION ALL SELECT id, domain, short_uri, full_short_url, origin_url, click_num, gid, favicon, enable_status, created_type, valid_date_type, valid_date, `describe`, total_pv, total_uv, total_uip, NULL, 0, create_time, update_time, del_time, del_flag FROM t_link_8
UNION ALL SELECT id, domain, short_uri, full_short_url, origin_url, click_num, gid, favicon, enable_status, created_type, valid_date_type, valid_date, `describe`, total_pv, total_uv, total_uip, NULL, 0, create_time, update_time, del_time, del_flag FROM t_link_9
UNION ALL SELECT id, domain, short_uri, full_short_url, origin_url, click_num, gid, favicon, enable_status, created_type, valid_date_type, valid_date, `describe`, total_pv, total_uv, total_uip, NULL, 0, create_time, update_time, del_time, del_flag FROM t_link_10
UNION ALL SELECT id, domain, short_uri, full_short_url, origin_url, click_num, gid, favicon, enable_status, created_type, valid_date_type, valid_date, `describe`, total_pv, total_uv, total_uip, NULL, 0, create_time, update_time, del_time, del_flag FROM t_link_11
UNION ALL SELECT id, domain, short_uri, full_short_url, origin_url, click_num, gid, favicon, enable_status, created_type, valid_date_type, valid_date, `describe`, total_pv, total_uv, total_uip, NULL, 0, create_time, update_time, del_time, del_flag FROM t_link_12
UNION ALL SELECT id, domain, short_uri, full_short_url, origin_url, click_num, gid, favicon, enable_status, created_type, valid_date_type, valid_date, `describe`, total_pv, total_uv, total_uip, NULL, 0, create_time, update_time, del_time, del_flag FROM t_link_13
UNION ALL SELECT id, domain, short_uri, full_short_url, origin_url, click_num, gid, favicon, enable_status, created_type, valid_date_type, valid_date, `describe`, total_pv, total_uv, total_uip, NULL, 0, create_time, update_time, del_time, del_flag FROM t_link_14
UNION ALL SELECT id, domain, short_uri, full_short_url, origin_url, click_num, gid, favicon, enable_status, created_type, valid_date_type, valid_date, `describe`, total_pv, total_uv, total_uip, NULL, 0, create_time, update_time, del_time, del_flag FROM t_link_15;

INSERT INTO t_link_goto (id, gid, full_short_url)
SELECT id, gid, full_short_url FROM t_link_goto_0
UNION ALL SELECT id, gid, full_short_url FROM t_link_goto_1
UNION ALL SELECT id, gid, full_short_url FROM t_link_goto_2
UNION ALL SELECT id, gid, full_short_url FROM t_link_goto_3
UNION ALL SELECT id, gid, full_short_url FROM t_link_goto_4
UNION ALL SELECT id, gid, full_short_url FROM t_link_goto_5
UNION ALL SELECT id, gid, full_short_url FROM t_link_goto_6
UNION ALL SELECT id, gid, full_short_url FROM t_link_goto_7
UNION ALL SELECT id, gid, full_short_url FROM t_link_goto_8
UNION ALL SELECT id, gid, full_short_url FROM t_link_goto_9
UNION ALL SELECT id, gid, full_short_url FROM t_link_goto_10
UNION ALL SELECT id, gid, full_short_url FROM t_link_goto_11
UNION ALL SELECT id, gid, full_short_url FROM t_link_goto_12
UNION ALL SELECT id, gid, full_short_url FROM t_link_goto_13
UNION ALL SELECT id, gid, full_short_url FROM t_link_goto_14
UNION ALL SELECT id, gid, full_short_url FROM t_link_goto_15;

-- 默认将 admin 用户提升为管理员角色
UPDATE t_user SET role = 'ADMIN' WHERE username = 'admin';
