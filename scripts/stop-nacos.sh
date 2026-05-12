#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

echo "[nacos] stopping nacos server ..."
docker compose -f docker-compose.nacos.yml down
echo "[nacos] done."
