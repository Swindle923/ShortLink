# ShortLink 单体化本地开发指南

## 1. 项目位置

```bash
/Users/heybox/Desktop/毕业设计/毕业设计项目version1/shortlink
```

## 2. 架构改造说明（微服务 -> 单体）

- 统一入口：使用 `project` 作为唯一后端服务入口（端口 `8003`）。
- 模块合并：已将 `admin` 模块合并至 `project`，统一路由。
- 消除服务间调用：`admin` Controller 直接依赖 `project` 的 Service 类，不再走 Feign HTTP。
- 运行模型：不依赖注册中心与网关，后端单进程直接提供管理与跳转能力。
- 统一跨域：新增 `MonolithCorsConfiguration`，支持来源、方法、头、凭证统一配置。

## 3. 运行时依赖

- Node.js LTS：`22.x`
- Java：`JDK 17+`（推荐 17）
- Maven：`3.9+`
- Docker / Docker Compose
- 数据库与缓存：
  - MySQL 8（业务主库）
  - Redis 7（缓存、登录态）
  - PostgreSQL 16（可选，兼容性驱动验证）

## 4. 环境变量

复制模板：

```bash
cp .env.example .env
```

默认关键配置：

- `APP_PORT=8003`
- `MYSQL_HOST=127.0.0.1`
- `MYSQL_PORT=3306`
- `MYSQL_USERNAME=root`
- `MYSQL_PASSWORD=root`
- `MYSQL_ROOT_PASSWORD=root`
- `MYSQL_DATABASE=link`
- `REDIS_HOST=127.0.0.1`
- `REDIS_PORT=6379`
- `REDIS_PASSWORD=123456`
- `CORS_ALLOWED_ORIGINS=http://localhost:5173,http://127.0.0.1:5173`

## 5. 一键启动（含数据库、缓存、服务）

```bash
./scripts/bootstrap-monolith.sh
```

脚本内部流程：

1. `start-deps.sh`：启动 MySQL / Redis 容器
2. `init-db.sh`：执行 `resources/database/link.sql` 与 `link-data.sql`
3. `start-monolith.sh`：构建并启动单体后端

如需把历史分表数据迁移到单表（全新项目可忽略），执行：

```bash
docker exec -i shortlink-mysql mysql -uroot -proot link < resources/database/migrate-shard-to-single.sql
```

## 6. 分步命令（调试时）

```bash
./scripts/check-runtime.sh
./scripts/start-deps.sh
./scripts/init-db.sh
./scripts/start-monolith.sh
```

## 7. 前端本地开发

```bash
cd console-vue
cp .env.example .env
/opt/homebrew/opt/node@22/bin/npm install
/opt/homebrew/opt/node@22/bin/npm run dev
```

前端默认开发地址 `http://localhost:5173`，已在后端 CORS 白名单。
前端代理默认会转发到 `http://127.0.0.1:8003`，可通过 `VITE_API_PROXY_TARGET` 覆盖。

## 8. 集成烟测

服务启动后执行：

```bash
./scripts/monolith-smoke-test.sh
```

默认校验：

- 用户注册
- 用户登录
- 短链生成
- 短链跳转
- 统计查询
- 所有请求：无 `5xx`，单次响应时间 `<= 0.2s`（可用 `MAX_TIME` 覆盖）

示例：

```bash
BASE_URL=http://127.0.0.1:8003 MAX_TIME=0.2 ./scripts/monolith-smoke-test.sh
```

## 9. 关于是否需要单独 Docker 化

建议保留“依赖容器化 + 应用本地进程”模式作为日常开发默认方案：

- 优点：调试最方便，启动快，IDE 断点体验最好。
- 适用：个人开发、毕设、本地联调。
- 若需要完整容器化（后端也进容器），可在此基础上补充 `Dockerfile` 与 compose service。

## 10. 数据库文档与建表脚本

- 数据库说明文档：`resources/database/DB_MANUAL.md`
- 单体建表脚本：`resources/database/link.sql`
- 初始化数据脚本：`resources/database/link-data.sql`

## 11. Vercel 部署说明

- 前端（`console-vue`）可直接部署到 Vercel。
- 后端（Spring Boot 单体）不适合直接部署到 Vercel 作为常驻服务：
  - Vercel 主要是静态站点与 Serverless Function 模型。
  - 当前后端是长生命周期 Java 服务，不是函数式入口。
- 推荐方案：
  - 前端：Vercel
  - 后端：Render / Railway / Fly.io / 云主机 Docker

## 12. 监控闭环（Prometheus + Grafana + Alertmanager）

启动监控栈：

```bash
./scripts/start-monitoring.sh
```

地址：

- Prometheus：`http://127.0.0.1:9090`
- Alertmanager：`http://127.0.0.1:9093`
- Grafana：`http://127.0.0.1:3000`

关闭监控栈：

```bash
./scripts/stop-monitoring.sh
```

详细说明见：`monitoring/README.md`

## 13. Nacos 动态配置中心

启动 Nacos：

```bash
./scripts/start-nacos.sh
```

初始化配置（示例 DataId）：

```bash
./scripts/nacos-publish-config.sh
```

使用 Nacos 配置启动后端：

```bash
./scripts/start-monolith-nacos.sh
```

停止 Nacos：

```bash
./scripts/stop-nacos.sh
```

详细文档见：`docs/nacos/README.md`
