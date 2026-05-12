#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

mkdir -p testing/reports/functional

echo "[test-platform] running functional tests ..."
docker compose -f docker-compose.test-platform.yml run --rm \
  --entrypoint newman test-newman run \
  /etc/newman/functional/shortlink-functional.postman_collection.json \
  -e /etc/newman/functional/local.postman_environment.json \
  --reporters cli,junit,json \
  --reporter-junit-export /etc/newman/reports/functional/newman-junit.xml \
  --reporter-json-export /etc/newman/reports/functional/newman-run.json

echo "[test-platform] functional done: testing/reports/functional/newman-run.json"
