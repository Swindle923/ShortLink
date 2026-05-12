#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://127.0.0.1:8003}"
USERNAME="${USERNAME:-smoke_user}"
PASSWORD="${PASSWORD:-smoke_pass_123}"
MAX_TIME="${MAX_TIME:-0.2}"

request_json() {
  local method="$1"
  local url="$2"
  local data="${3:-}"
  local username_header="${4:-}"
  local tmp_body
  tmp_body="$(mktemp)"
  local out
  if [[ -n "$data" ]]; then
    out=$(curl -sS -o "$tmp_body" -w "%{http_code} %{time_total}" \
      -X "$method" "$url" \
      -H "Content-Type: application/json" \
      -H "Username: $username_header" \
      --data "$data")
  else
    out=$(curl -sS -o "$tmp_body" -w "%{http_code} %{time_total}" \
      -X "$method" "$url" \
      -H "Username: $username_header")
  fi
  echo "$out|$tmp_body"
}

assert_status_and_time() {
  local output="$1"
  local http_code="${output%% *}"
  local time_total="${output##* }"
  if [[ "$http_code" -ge 500 ]]; then
    echo "接口返回 5xx: $http_code"
    exit 1
  fi
  awk -v t="$time_total" -v max="$MAX_TIME" 'BEGIN { if (t > max) exit 1; }' || {
    echo "响应时间超标: ${time_total}s > ${MAX_TIME}s"
    exit 1
  }
}

extract_json_field() {
  local file="$1"
  local expr="$2"
  python3 - "$file" "$expr" <<'PY'
import json,sys
p=sys.argv[2].split(".")
obj=json.load(open(sys.argv[1], "r", encoding="utf-8"))
for k in p:
    if k.isdigit():
        obj=obj[int(k)]
    else:
        obj=obj.get(k)
print("" if obj is None else obj)
PY
}

echo "== 注册用户（已存在则忽略） =="
register='{"username":"'"$USERNAME"'","password":"'"$PASSWORD"'","realName":"smoke","phone":"13800000000","mail":"smoke@example.com"}'
res="$(request_json POST "$BASE_URL/api/short-link/admin/v1/user" "$register" "")"
meta="${res%%|*}"
body="${res##*|}"
assert_status_and_time "$meta"
rm -f "$body"

echo "== 登录 =="
login='{"username":"'"$USERNAME"'","password":"'"$PASSWORD"'"}'
res="$(request_json POST "$BASE_URL/api/short-link/admin/v1/user/login" "$login" "")"
meta="${res%%|*}"
body="${res##*|}"
assert_status_and_time "$meta"
token="$(extract_json_field "$body" "data.token")"
if [[ -z "$token" ]]; then
  echo "登录未返回 token"
  cat "$body"
  exit 1
fi
rm -f "$body"

echo "== 创建分组 =="
group='{"name":"smoke-group"}'
res="$(request_json POST "$BASE_URL/api/short-link/admin/v1/group" "$group" "$USERNAME")"
meta="${res%%|*}"
body="${res##*|}"
assert_status_and_time "$meta"
rm -f "$body"

echo "== 查询分组并获取 gid =="
res="$(request_json GET "$BASE_URL/api/short-link/admin/v1/group" "" "$USERNAME")"
meta="${res%%|*}"
body="${res##*|}"
assert_status_and_time "$meta"
gid="$(extract_json_field "$body" "data.0.gid")"
if [[ -z "$gid" ]]; then
  echo "未获取到 gid"
  cat "$body"
  exit 1
fi
rm -f "$body"

echo "== 创建短链 =="
create='{"domain":"nurl.ink:8003","originUrl":"https://zhihu.com","gid":"'"$gid"'","createdType":1,"validDateType":0,"describe":"smoke"}'
res="$(request_json POST "$BASE_URL/api/short-link/admin/v1/create" "$create" "$USERNAME")"
meta="${res%%|*}"
body="${res##*|}"
assert_status_and_time "$meta"
full_short_url="$(extract_json_field "$body" "data.fullShortUrl")"
short_uri="$(extract_json_field "$body" "data.shortUri")"
if [[ -z "$full_short_url" || -z "$short_uri" ]]; then
  echo "创建短链响应缺失字段"
  cat "$body"
  exit 1
fi
rm -f "$body"

echo "== 跳转验证 =="
jump="$(curl -sS -o /dev/null -w "%{http_code} %{time_total}" "$BASE_URL/$short_uri")"
assert_status_and_time "$jump"

echo "== 统计查询 =="
stats_url="$BASE_URL/api/short-link/admin/v1/stats?fullShortUrl=$full_short_url&gid=$gid&enableStatus=0&startDate=2024-01-01&endDate=2030-01-01"
res="$(request_json GET "$stats_url" "" "$USERNAME")"
meta="${res%%|*}"
body="${res##*|}"
assert_status_and_time "$meta"
rm -f "$body"

echo "Smoke Test 通过：注册、登录、短链生成、跳转、统计均正常，且无 5xx、耗时 <= ${MAX_TIME}s"
