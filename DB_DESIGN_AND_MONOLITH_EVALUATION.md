# ShortLink 数据库说明与去分片评估

## 1. 当前数据库概览

- 数据库名：`link`
- 存储引擎：`InnoDB`
- 字符集：`utf8mb4`
- 软删除字段：多数业务表使用 `del_flag`
- 分片形态：当前是**单库 + 分表**（不是多库）

`link.sql` 中共 73 张表，构成如下：

- 分片表（64 张）
  - `t_user_0 ~ t_user_15`（16 张）
  - `t_group_0 ~ t_group_15`（16 张）
  - `t_link_0 ~ t_link_15`（16 张）
  - `t_link_goto_0 ~ t_link_goto_15`（16 张）
- 非分片表（9 张）
  - `t_group_unique`
  - `t_link_access_logs`
  - `t_link_access_stats`
  - `t_link_browser_stats`
  - `t_link_device_stats`
  - `t_link_locale_stats`
  - `t_link_network_stats`
  - `t_link_os_stats`
  - `t_link_stats_today`

## 2. 分片与加密规则（ShardingSphere）

当前 `admin` / `project` / `aggregation` 的 `shardingsphere-config-dev.yaml` 规则基本一致：

- `t_user`：按 `username` 做 `HASH_MOD(16)` 路由到 `t_user_0..15`
- `t_group`：按 `username` 做 `HASH_MOD(16)` 路由到 `t_group_0..15`
- `t_link`：按 `gid` 做 `HASH_MOD(16)` 路由到 `t_link_0..15`
- `t_link_goto`：按 `full_short_url` 做 `HASH_MOD(16)` 路由到 `t_link_goto_0..15`

同时启用了 ShardingSphere 加密规则：

- 逻辑表：`t_user`
- 字段：`phone`、`mail`
- 算法：AES（`common_encryptor`）
- 配置：`queryWithCipherColumn: true`

这意味着当前手机号和邮箱的“透明加解密”由 ShardingSphere JDBC 层负责。

## 3. 核心业务表说明

### 3.1 用户与分组

- `t_user_*`
  - 主键：`id`
  - 唯一索引：`username`
  - 关键字段：`password`, `real_name`, `phone`, `mail`, `deletion_time`, `del_flag`
- `t_group_*`
  - 主键：`id`
  - 常用索引：`idx_username(username)`
  - 关键字段：`gid`, `name`, `username`, `sort_order`, `del_flag`
- `t_group_unique`
  - 用于保障 `gid` 全局唯一（唯一索引 `idx_unique_gid(gid)`）

### 3.2 短链主数据

- `t_link_*`
  - 主键：`id`
  - 唯一索引：`(full_short_url, del_time)`
  - 关键字段：
    - 基本信息：`domain`, `short_uri`, `full_short_url`, `origin_url`, `gid`
    - 状态信息：`enable_status`, `valid_date_type`, `valid_date`, `del_flag`, `del_time`
    - 统计汇总：`total_pv`, `total_uv`, `total_uip`, `click_num`
- `t_link_goto_*`
  - 主键：`id`
  - 唯一索引：`full_short_url`
  - 关键字段：`gid`, `full_short_url`
  - 用途：短链跳转时快速定位目标分组与映射

### 3.3 统计分析

- 明细日志：`t_link_access_logs`
- 按小时/日聚合：`t_link_access_stats`
- 维度统计：
  - 浏览器：`t_link_browser_stats`
  - 设备：`t_link_device_stats`
  - 地域：`t_link_locale_stats`
  - 网络：`t_link_network_stats`
  - 操作系统：`t_link_os_stats`
- 当日快照：`t_link_stats_today`

统计表主维度基本都围绕 `full_short_url + date (+ 维度字段)` 建唯一索引。

## 4. 关系与数据流（逻辑）

- 用户 `username` -> 分组 `t_group.username`
- 分组 `gid` -> 短链 `t_link.gid`
- 短链 `full_short_url` -> 跳转映射 `t_link_goto.full_short_url`
- 短链 `full_short_url` -> 所有统计表 `full_short_url`

项目中实体基本都使用逻辑表名（如 `@TableName("t_user")`、`@TableName("t_link")`），目前依赖 ShardingSphere 做路由到真实分片表。

## 5. 是否可以去掉分库分表改为单库单表？

结论：**可以，且在你当前毕设/本地开发场景是可行且推荐的。**

但要注意：这不是只改配置，至少要处理以下两件事：

1. 去掉分片路由（`t_user` 不再映射 `t_user_0..15`）
2. 处理 `t_user.phone/mail` 的透明加解密能力迁移（当前依赖 ShardingSphere ENCRYPT）

## 6. 改造成单库单表的改造清单

### 6.1 配置层

- `admin` / `project` / `aggregation` 的 `application.yaml`
  - `driver-class-name` 改为 `com.mysql.cj.jdbc.Driver`
  - `url` 改为普通 MySQL JDBC URL（不再用 `jdbc:shardingsphere:...`）
- 移除对 `shardingsphere-config-*.yaml` 的依赖

### 6.2 依赖层

- `admin/pom.xml`、`project/pom.xml` 去掉 `shardingsphere-jdbc-core`
- 顶层 `pom.xml` 的 `dependencyManagement` 去掉该依赖版本管理

### 6.3 数据库层

新增 4 张单表并迁移数据：

- `t_user`（由 `t_user_0..15` 合并）
- `t_group`（由 `t_group_0..15` 合并）
- `t_link`（由 `t_link_0..15` 合并）
- `t_link_goto`（由 `t_link_goto_0..15` 合并）

非分片统计表保持不变。

### 6.4 代码层（重点）

- 因实体表名本来是逻辑表名（`t_user`/`t_link`），DAO 大概率无需大改。
- 但若有 SQL 直接访问物理分表（`*_0..15`）需替换为单表（建议全局 grep 检查）。
- 若保留敏感字段加密要求，需要在应用层补充加解密（JPA/MyBatis TypeHandler/Service 层）替代 ShardingSphere ENCRYPT。

### 6.5 测试与验证

- 注册/登录（重点验证 `phone/mail` 行为）
- 分组 CRUD
- 短链创建、更新、回收站
- 跳转与监控统计
- 性能回归（单表索引命中、慢 SQL）

## 7. 数据迁移建议（安全做法）

建议采用“并行迁移 + 回切”：

1. 新建单表（保留当前 64 张分片表不动）
2. `INSERT INTO 单表 SELECT ... FROM 分片表` 合并数据
3. 校验行数与关键唯一性
4. 应用切到单表配置并灰度验证
5. 稳定后再归档/清理旧分片表

关键风险点：

- 唯一索引冲突（尤其 `username`、`full_short_url`）
- 历史数据中的脏数据在单表唯一约束下暴露
- 手机号/邮箱加密语义变化导致查询不兼容

## 8. 对你当前项目的评估结论

- 技术上可改：**是**
- 改造成本：**中等**（配置/依赖简单，数据迁移和加密策略是难点）
- 业务风险：**可控**（前提是做完整迁移校验和回滚预案）
- 建议优先级：**高**（毕设/单机开发用单库单表更稳、调试更直观）

---

如果你需要，我下一步可以直接给你产出：

1. `link_single.sql`（单表版建表脚本）  
2. `migrate_shard_to_single.sql`（从 64 张分片表合并到 4 张单表）  
3. 三个模块去 ShardingSphere 的最小改造 patch（含 `pom` + `application.yaml`）。
