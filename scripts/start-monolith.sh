#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
JAVA_HOME_CANDIDATE="${JAVA_HOME:-/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home}"
MVN_BIN="${MVN_BIN:-/opt/homebrew/bin/mvn}"

cd "$ROOT_DIR"
if [[ -f .env ]]; then
  set -a
  source .env
  set +a
fi
export JAVA_HOME="$JAVA_HOME_CANDIDATE"
export PATH="$JAVA_HOME/bin:/opt/homebrew/opt/node@22/bin:/opt/homebrew/bin:$PATH"
mkdir -p "$ROOT_DIR/.sentinel-logs"
export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} -Dcsp.sentinel.log.dir=$ROOT_DIR/.sentinel-logs"

"$MVN_BIN" -pl aggregation -am -DskipTests package

exec "$JAVA_HOME/bin/java" -jar "$ROOT_DIR/aggregation/target/shortlink-aggregation.jar"
