# 测试平台（功能 + 性能）

本目录提供一套可部署的统一测试平台：

- 功能测试：Newman（Postman Collection）
- 性能测试：k6
- 报告查看：Nginx 静态页面

## 目录结构
- `functional/`：功能测试集合与环境变量
- `performance/`：k6 压测脚本
- `reports/`：测试报告输出目录

## 一键执行
在项目根目录执行：

```bash
bash ./scripts/run-all-tests.sh
```

执行后报告地址：

- `http://127.0.0.1:8099`

## 单独执行
启动报告 UI：

```bash
bash ./scripts/start-test-platform.sh
```

只跑功能测试：

```bash
bash ./scripts/run-functional-tests.sh
```

只跑性能测试：

```bash
bash ./scripts/run-performance-tests.sh
```

停止测试平台：

```bash
bash ./scripts/stop-test-platform.sh
```

## 可调参数
- `BASE_URL`（默认 `http://host.docker.internal:8003`）
- `VUS`（默认 `20`）
- `DURATION`（默认 `30s`）
- `TEST_REPORT_PORT`（默认 `8099`）
