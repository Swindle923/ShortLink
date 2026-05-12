#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
if [[ -f "$ROOT_DIR/.env" ]]; then
  set -a
  source "$ROOT_DIR/.env"
  set +a
fi

MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-${MYSQL_PASSWORD:-root}}"
MYSQL_DATABASE="${MYSQL_DATABASE:-link}"

echo "== 初始化 MySQL 数据库 =="
docker exec -i shortlink-mysql mysql -uroot "-p${MYSQL_ROOT_PASSWORD}" -e "CREATE DATABASE IF NOT EXISTS ${MYSQL_DATABASE} DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
docker exec -i shortlink-mysql mysql -uroot "-p${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" < "$ROOT_DIR/resources/database/link.sql"
docker exec -i shortlink-mysql mysql -uroot "-p${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" < "$ROOT_DIR/resources/database/link-data.sql"

echo "== 校验核心表数量 =="
docker exec -i shortlink-mysql mysql -uroot "-p${MYSQL_ROOT_PASSWORD}" -N -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='${MYSQL_DATABASE}';"
echo "数据库初始化完成"
