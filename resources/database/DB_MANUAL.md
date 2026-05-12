# ShortLink 数据库说明文档（单库单表版）

## 1. 基础信息

- 数据库名：`link`
- 数据库类型：`MySQL 8.x`
- 字符集：`utf8mb4`
- 存储引擎：`InnoDB`
- 命名风格：业务表统一前缀 `t_`
- 软删除策略：主要业务表使用 `del_flag`（`0` 未删除，`1` 已删除）

## 2. 导入方式

### 2.1 仅建表

执行：

```sql
SOURCE resources/database/create-tables.sql;
```

### 2.2 建表 + 初始化数据

执行：

```sql
SOURCE resources/database/link.sql;
SOURCE resources/database/link-data.sql;
```

## 3. 表清单（12 张）

### 3.1 用户与分组

- `t_user`：用户账号信息
- `t_group`：短链分组信息
- `t_group_unique`：分组唯一约束辅助表（`gid` 唯一）

### 3.2 短链核心

- `t_link`：短链主数据
- `t_link_goto`：短链跳转路由映射

### 3.3 访问统计

- `t_link_access_logs`：访问明细日志
- `t_link_access_stats`：按日期/小时聚合统计
- `t_link_stats_today`：当日汇总统计
- `t_link_browser_stats`：浏览器维度统计
- `t_link_os_stats`：操作系统维度统计
- `t_link_network_stats`：网络维度统计
- `t_link_locale_stats`：地域维度统计
- `t_link_device_stats`：设备维度统计

## 4. 核心关系说明

- 用户分组关系：`t_group.username -> t_user.username`
- 分组与短链关系：`t_link.gid -> t_group.gid`
- 跳转映射关系：`t_link_goto.full_short_url -> t_link.full_short_url`
- 统计关系：各统计表通过 `full_short_url` 关联 `t_link`

## 5. 关键索引

- `t_user.idx_unique_username`：用户名唯一
- `t_group_unique.idx_unique_gid`：分组唯一
- `t_link.idx_unique_full-short-url`：短链唯一（结合 `del_time`）
- `t_link_goto.idx_full_short_url`：跳转快速命中
- 统计表均有 `full_short_url + date (+维度)` 唯一索引，保证聚合幂等

## 6. 关键字段约定

- `gid`：业务分组 ID（字符串）
- `full_short_url`：完整短链（域名 + URI）
- `origin_url`：原始长链接
- `valid_date_type`：有效期类型（`0` 永久，`1` 自定义）
- `enable_status`：短链状态（`0` 启用，`1` 禁用）
- `del_time`：删除时间戳（用于唯一索引与回收）

## 7. 初始化账号

`link-data.sql` 默认会插入管理员账号：

- 用户名：`admin`
- 密码：`admin123456`

建议首次启动后立即修改默认密码。
