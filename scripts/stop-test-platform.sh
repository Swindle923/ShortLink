#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

echo "[test-platform] stopping ..."
docker compose -f docker-compose.test-platform.yml down
echo "[test-platform] done."
