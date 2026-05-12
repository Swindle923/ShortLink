#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

if [[ -f .env ]]; then
  set -a
  source .env
  set +a
fi

export PROMETHEUS_PORT="${PROMETHEUS_PORT:-9090}"
export GRAFANA_PORT="${GRAFANA_PORT:-3000}"
export ALERTMANAGER_PORT="${ALERTMANAGER_PORT:-9093}"
export REDIS_EXPORTER_PORT="${REDIS_EXPORTER_PORT:-9121}"
export LOKI_PORT="${LOKI_PORT:-3100}"

echo "[monitoring] starting Prometheus/Alertmanager/Grafana/Redis-Exporter ..."
docker compose -f docker-compose.monitoring.yml up -d

echo "[monitoring] done."
echo "Prometheus  : http://127.0.0.1:${PROMETHEUS_PORT}"
echo "Alertmanager: http://127.0.0.1:${ALERTMANAGER_PORT}"
echo "Grafana     : http://127.0.0.1:${GRAFANA_PORT}"
echo "Loki        : http://127.0.0.1:${LOKI_PORT}"
echo "Grafana 用户名/密码: ${GRAFANA_ADMIN_USER:-admin} / ${GRAFANA_ADMIN_PASSWORD:-admin123456}"
