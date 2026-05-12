#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

if [[ -f .env ]]; then
  set -a
  source .env
  set +a
fi

export TEST_REPORT_PORT="${TEST_REPORT_PORT:-8099}"

mkdir -p testing/reports testing/reports/functional testing/reports/performance

if [[ ! -f testing/reports/index.html ]]; then
  cat > testing/reports/index.html <<'HTML'
<!doctype html>
<html>
  <head><meta charset="utf-8"><title>ShortLink Test Reports</title></head>
  <body>
    <h2>ShortLink Test Reports</h2>
    <ul>
      <li><a href="./functional/newman-run.json">Functional Result (Newman JSON)</a></li>
      <li><a href="./performance/k6-summary.json">Performance Result (k6 JSON)</a></li>
    </ul>
  </body>
</html>
HTML
fi

echo "[test-platform] starting report ui ..."
docker compose -f docker-compose.test-platform.yml up -d test-report-ui
echo "[test-platform] report ui: http://127.0.0.1:${TEST_REPORT_PORT}"
