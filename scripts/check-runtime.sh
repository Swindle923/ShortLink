#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
if [[ -f "$ROOT_DIR/.env" ]]; then
  set -a
  source "$ROOT_DIR/.env"
  set +a
fi

JAVA_HOME_CANDIDATE="${JAVA_HOME:-/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home}"
JAVA_BIN="${JAVA_HOME_CANDIDATE}/bin/java"
MVN_BIN="${MVN_BIN:-/opt/homebrew/bin/mvn}"
NODE_BIN="${NODE_BIN:-/opt/homebrew/opt/node@22/bin/node}"
NPM_BIN="${NPM_BIN:-/opt/homebrew/opt/node@22/bin/npm}"

ok() { echo "[OK] $*"; }
warn() { echo "[WARN] $*"; }
err() { echo "[ERR] $*"; exit 1; }

echo "== Runtime Check =="

if [[ -x "$JAVA_BIN" ]]; then
  ok "Java: $("$JAVA_BIN" -version 2>&1 | head -n 1)"
else
  warn "未找到可执行 Java: $JAVA_BIN"
  warn "可尝试: brew install openjdk@17 && export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
fi

if [[ -x "$MVN_BIN" ]]; then
  ok "Maven: $("$MVN_BIN" -v | head -n 1)"
else
  err "未找到 Maven，请安装: brew install maven"
fi

if [[ -x "$NODE_BIN" ]]; then
  ok "Node.js: $("$NODE_BIN" -v)"
else
  err "未找到 Node.js LTS，请安装: brew install node@22"
fi

if [[ -x "$NPM_BIN" ]]; then
  ok "npm: $("$NPM_BIN" -v)"
else
  err "未找到 npm，请检查 node@22 安装"
fi

if command -v docker >/dev/null 2>&1; then
  ok "Docker: $(docker --version)"
else
  err "未找到 Docker"
fi

if docker compose version >/dev/null 2>&1; then
  ok "Docker Compose: $(docker compose version)"
else
  err "未找到 docker compose 子命令"
fi

echo "== Build Compatibility Check =="
(
  cd "$ROOT_DIR"
  export JAVA_HOME="$JAVA_HOME_CANDIDATE"
  export PATH="$JAVA_HOME/bin:/opt/homebrew/opt/node@22/bin:/opt/homebrew/bin:$PATH"
  "$MVN_BIN" -pl aggregation -am -DskipTests package >/tmp/shortlink-monolith-build.log
)
ok "Maven 构建通过（日志: /tmp/shortlink-monolith-build.log）"
