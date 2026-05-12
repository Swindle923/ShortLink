#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

mkdir -p testing/reports/performance

BASE_URL="${BASE_URL:-http://host.docker.internal:8003}"
VUS="${VUS:-20}"
DURATION="${DURATION:-30s}"

echo "[test-platform] running performance tests ..."
docker compose -f docker-compose.test-platform.yml run --rm \
  --entrypoint k6 test-k6 run \
  --out json=/workspace/reports/performance/k6-summary.json \
  -e BASE_URL="${BASE_URL}" \
  -e VUS="${VUS}" \
  -e DURATION="${DURATION}" \
  /workspace/performance/smoke.js

echo "[test-platform] performance done: testing/reports/performance/k6-summary.json"
