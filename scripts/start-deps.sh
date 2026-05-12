#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

cd "$ROOT_DIR"
if [[ -f .env ]]; then
  set -a
  source .env
  set +a
fi

MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-${MYSQL_PASSWORD:-root}}"
docker compose -f docker-compose.monolith.yml up -d mysql redis

echo "等待 MySQL 就绪..."
for _ in $(seq 1 60); do
  if docker exec shortlink-mysql mysqladmin ping -uroot "-p${MYSQL_ROOT_PASSWORD}" --silent >/dev/null 2>&1; then
    echo "MySQL 已就绪"
    exit 0
  fi
  sleep 2
done

echo "MySQL 启动超时" >&2
exit 1
