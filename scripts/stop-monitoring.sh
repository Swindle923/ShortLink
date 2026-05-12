#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

echo "[monitoring] stopping Prometheus/Alertmanager/Grafana/Redis-Exporter ..."
docker compose -f docker-compose.monitoring.yml down
echo "[monitoring] done."
