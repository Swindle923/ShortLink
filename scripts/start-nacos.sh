#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

if [[ -f .env ]]; then
  set -a
  source .env
  set +a
fi

export NACOS_PORT="${NACOS_PORT:-8848}"
export NACOS_RPC_PORT="${NACOS_RPC_PORT:-9848}"
export NACOS_RAFT_PORT="${NACOS_RAFT_PORT:-9849}"

echo "[nacos] starting nacos server ..."
docker compose -f docker-compose.nacos.yml up -d

echo "[nacos] waiting for ready ..."
for _ in $(seq 1 60); do
  if curl -sf "http://127.0.0.1:${NACOS_PORT}/nacos/v1/console/health/liveness" >/dev/null; then
    echo "[nacos] ready: http://127.0.0.1:${NACOS_PORT}/nacos"
    exit 0
  fi
  sleep 2
done

echo "[nacos] startup timeout" >&2
exit 1
