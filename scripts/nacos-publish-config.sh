#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

if [[ -f .env ]]; then
  set -a
  source .env
  set +a
fi

NACOS_PORT="${NACOS_PORT:-8848}"
NACOS_ADDR="${NACOS_SERVER_ADDR:-127.0.0.1:${NACOS_PORT}}"
NACOS_GROUP="${NACOS_GROUP:-SHORTLINK_GROUP}"
NACOS_NAMESPACE="${NACOS_NAMESPACE:-shortlink-dev}"
NACOS_USERNAME="${NACOS_USERNAME:-nacos}"
NACOS_PASSWORD="${NACOS_PASSWORD:-nacos}"

common_content=$'short-link:\n  flow-limit:\n    enable: true\n    time-window: 1\n    max-access-count: 20\n  goto-domain:\n    white-list:\n      enable: false\n      names: \"拿个offer,知乎,掘金,博客园\"\n      details:\n        - nageoffer.com\n        - zhihu.com\n        - juejin.cn\n        - cnblogs.com\n'

service_content=$'short-link:\n  demo-mode:\n    enable: false\n    blacklist:\n      - /api/short-link/admin/v1/group\n      - /api/short-link/admin/v1/recycle-bin/remove\n  cors:\n    allowed-origins: http://localhost:5173,http://127.0.0.1:5173\n    allowed-methods: GET,POST,PUT,DELETE,OPTIONS\n    allowed-headers: \"*\"\n    allow-credentials: true\n'

echo "[nacos] ensuring namespace ${NACOS_NAMESPACE} ..."
namespace_list="$(curl -sf "http://${NACOS_ADDR}/nacos/v1/console/namespaces?username=${NACOS_USERNAME}&password=${NACOS_PASSWORD}" || true)"
if ! echo "${namespace_list}" | grep -q "\"namespace\":\"${NACOS_NAMESPACE}\""; then
  curl -sf -X POST "http://${NACOS_ADDR}/nacos/v1/console/namespaces" \
    --data-urlencode "customNamespaceId=${NACOS_NAMESPACE}" \
    --data-urlencode "namespaceName=ShortLink Dev" \
    --data-urlencode "namespaceDesc=ShortLink dedicated namespace" \
    --data-urlencode "username=${NACOS_USERNAME}" \
    --data-urlencode "password=${NACOS_PASSWORD}" >/dev/null || true
fi

echo "[nacos] publishing shortlink-common.yaml ..."
curl -sf -X POST "http://${NACOS_ADDR}/nacos/v1/cs/configs" \
  --data-urlencode "dataId=shortlink-common.yaml" \
  --data-urlencode "group=${NACOS_GROUP}" \
  --data-urlencode "tenant=${NACOS_NAMESPACE}" \
  --data-urlencode "type=yaml" \
  --data-urlencode "username=${NACOS_USERNAME}" \
  --data-urlencode "password=${NACOS_PASSWORD}" \
  --data-urlencode "content=${common_content}" >/dev/null

echo "[nacos] publishing shortlink.yaml ..."
curl -sf -X POST "http://${NACOS_ADDR}/nacos/v1/cs/configs" \
  --data-urlencode "dataId=shortlink.yaml" \
  --data-urlencode "group=${NACOS_GROUP}" \
  --data-urlencode "tenant=${NACOS_NAMESPACE}" \
  --data-urlencode "type=yaml" \
  --data-urlencode "username=${NACOS_USERNAME}" \
  --data-urlencode "password=${NACOS_PASSWORD}" \
  --data-urlencode "content=${service_content}" >/dev/null

echo "[nacos] publish done."
