#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

bash ./scripts/start-test-platform.sh
bash ./scripts/run-functional-tests.sh
bash ./scripts/run-performance-tests.sh

echo "[test-platform] all done."
