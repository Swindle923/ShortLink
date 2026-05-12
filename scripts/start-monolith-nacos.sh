#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

if [[ -f .env ]]; then
  set -a
  source .env
  set +a
fi

export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-nacos}"
export NACOS_SERVER_ADDR="${NACOS_SERVER_ADDR:-127.0.0.1:8848}"
export NACOS_GROUP="${NACOS_GROUP:-SHORTLINK_GROUP}"
export NACOS_NAMESPACE="${NACOS_NAMESPACE:-shortlink-dev}"
export NACOS_USERNAME="${NACOS_USERNAME:-nacos}"
export NACOS_PASSWORD="${NACOS_PASSWORD:-nacos}"

exec ./scripts/start-monolith.sh
