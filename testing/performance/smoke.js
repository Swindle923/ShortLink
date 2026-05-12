import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  vus: Number(__ENV.VUS || 20),
  duration: __ENV.DURATION || "30s",
  thresholds: {
    http_req_failed: ["rate<0.05"],
    http_req_duration: ["p(95)<800"]
  }
};

const baseUrl = __ENV.BASE_URL || "http://host.docker.internal:8003";

export default function () {
  const res = http.get(`${baseUrl}/actuator/health`);
  check(res, {
    "status is 200": (r) => r.status === 200
  });
  sleep(0.2);
}
