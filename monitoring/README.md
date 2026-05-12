# 监控闭环（Prometheus + Alertmanager + Grafana）

本目录提供 ShortLink 单体模式下的完整监控体系：
- 指标采集：Spring Boot Actuator + Micrometer + Prometheus
- 日志采集：Promtail + Loki
- 告警管理：Prometheus Rules + Alertmanager
- 可视化：Grafana 自动加载仪表盘

## 1. 前置条件
- 后端已启动：`http://127.0.0.1:8003`
- 依赖已启动：MySQL、Redis
- Docker Desktop 已启动

## 2. 一键启动监控
在项目根目录执行：

```bash
./scripts/start-monitoring.sh
```

启动后地址：
- Prometheus: `http://127.0.0.1:9090`
- Alertmanager: `http://127.0.0.1:9093`
- Grafana: `http://127.0.0.1:3000`
- Loki: `http://127.0.0.1:3100`

默认 Grafana 账号：
- 用户名：`admin`
- 密码：`admin123456`

## 3. 指标与看板
Grafana 已自动加载仪表盘：
- Dashboard: `ShortLink Service Overview`
- UID: `shortlink-overview`
- Dashboard: `ShortLink Business Chain`
- UID: `shortlink-business-chain`
- Dashboard: `ShortLink API Observability`
- UID: `shortlink-api-observability`
- Dashboard: `ShortLink JVM Runtime`
- UID: `shortlink-jvm-runtime`
- Dashboard: `ShortLink Redis Deep Dive`
- UID: `shortlink-redis-deep-dive`
- Dashboard: `ShortLink SLO & Error Budget`
- UID: `shortlink-slo-error-budget`
- Dashboard: `ShortLink Ops Operational Excellence`
- UID: `shortlink-ops-operational-excellence`

覆盖维度：
- 实时 QPS
- P95 响应时间
- 5xx 错误率
- JVM Heap 使用率
- JVM 平均 GC 停顿
- Redis 内存与命令吞吐
- 用户中心链路（注册/登录/登出/角色变更）
- 短链接链路（创建/更新/配额消费）
- 回收站链路（失效/恢复/删除）
- 统计分析链路（单链统计/分组统计/访问日志）
- API 维度（TopQPS、TopP95、状态码分布、错误率、延迟热力图）
- JVM 深度（Heap/GC/线程/类加载/Tomcat线程池/CPU）
- Redis 深度（可用性、命中率、连接、网络 IO、淘汰与慢日志）
- SLO 维度（可用性、错误预算、燃烧率、延迟目标达标）
- 运营增强（分组漏斗、配额健康度、高风险短链、SLO 分级命中）
- 日志维度（容器日志聚合、按服务过滤、关键字检索）

## 4. 日志系统（Loki）
Grafana 已预置 Loki 数据源，进入 `Explore` 可直接查询日志。

常用 LogQL 示例：

```logql
{container="shortlink"}
```

```logql
{container=~"shortlink-(aggregation|prometheus|redis-exporter)"} |= "error"
```

```logql
{compose_service="grafana"} |= "HTTP"
```

## 5. 代码埋点工具（监控工具抽象）
### 4.1 自动埋点（AOP）
- 文件：`aggregation/.../ServiceMetricsAspect.java`
- 作用：自动拦截 admin/project 的 service.impl 方法，统一产出：
  - `shortlink_biz_service_calls_total`
  - `shortlink_biz_service_latency_seconds`
- 标签：`module/class/method/result/exception`

### 4.2 手工埋点工具（BizMetricsKit）
- admin：`admin/.../BizMetricsKit.java`
- project：`project/.../BizMetricsKit.java`
- 用法示例：

```java
bizMetricsKit.increment("shortlink.biz.shortlink.create", "result", "success", "mode", "single");
bizMetricsKit.recordLatency("shortlink.biz.custom.latency", costNanos, "scene", "demo");
```

说明：后续你在任意业务代码位置都可以复用同一套 API 打点，减少“临时拼接指标名”的维护成本。

## 6. 告警规则
文件：`monitoring/prometheus/alert.rules.yml`

已内置规则：
- `ShortLinkAggregationDown`：服务不可用
- `ShortLinkHighErrorRate`：5xx 错误率 > 5%
- `ShortLinkP95LatencyHigh`：P95 > 800ms
- `ShortLinkJvmHeapHigh`：堆使用率 > 85%
- `ShortLinkJvmGcPauseHigh`：平均 GC 停顿 > 200ms
- `ShortLinkRedisDown`：Redis 不可用
- `ShortLinkRedisMemoryHigh`：Redis 内存 > 80%
- `ShortLinkSLOP1AvailabilityDrop`：P1，可用性严重下降
- `ShortLinkSLOP1ErrorBudgetBurnFast`：P1，错误预算超高速燃烧
- `ShortLinkSLOP2ErrorBudgetBurnHigh`：P2，错误预算持续高燃烧
- `ShortLinkSLOP3LatencyDegraded`：P3，延迟目标持续未达标

## 7. 告警触发验收（建议）
### 5.1 验证 QPS/P95 可见
执行压测命令（示例）：

```bash
for i in {1..200}; do curl -s http://127.0.0.1:8003/actuator/health >/dev/null; done
```

在 Grafana 观察 QPS/P95 曲线实时变化。

### 5.2 验证错误率告警
可对不存在接口持续请求，制造 5xx/4xx（按你服务返回行为）：

```bash
for i in {1..300}; do curl -s http://127.0.0.1:8003/api/short-link/admin/v1/not-exists >/dev/null; done
```

然后在 Prometheus `Alerts` 页面观察告警状态变化。

### 5.3 验证 Redis 告警
临时停止 Redis 容器：

```bash
docker stop shortlink-redis
```

等待 1 分钟后应触发 `ShortLinkRedisDown`。

恢复：

```bash
docker start shortlink-redis
```

## 8. 告警通知通道
Alertmanager 当前配置了 webhook 示例地址：
- `http://host.docker.internal:18080/alert`

如果你有企业微信机器人/飞书/钉钉中转服务，可在以下文件替换接收地址：
- `monitoring/alertmanager/alertmanager.yml`

修改后重启监控栈：

```bash
./scripts/stop-monitoring.sh
./scripts/start-monitoring.sh
```
