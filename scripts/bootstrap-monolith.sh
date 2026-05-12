#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

"$ROOT_DIR/scripts/start-deps.sh"
"$ROOT_DIR/scripts/init-db.sh"
"$ROOT_DIR/scripts/start-monolith.sh"
