package com.nageoffer.shortlink.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkOpsLifecycleAlertRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkOpsOverviewRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkOpsRiskRespDTO;
import com.nageoffer.shortlink.project.service.ops.ShortLinkOpsInsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "shortLinkOpsControllerByAdmin")
@RequiredArgsConstructor
public class ShortLinkOpsController {

    private final ShortLinkOpsInsightService shortLinkOpsInsightService;

    @GetMapping("/api/short-link/admin/v1/ops/overview")
    public Result<ShortLinkOpsOverviewRespDTO> opsOverview(String gid,
                                                           Integer expiringDays,
                                                           Double quotaRiskThreshold) {
        return Results.success(shortLinkOpsInsightService.buildOverview(gid, expiringDays, quotaRiskThreshold));
    }

    @GetMapping("/api/short-link/admin/v1/ops/high-risk")
    public Result<Page<ShortLinkOpsRiskRespDTO>> opsHighRisk(String gid, Long current, Long size) {
        return Results.success(shortLinkOpsInsightService.pageHighRisk(gid, current, size));
    }

    @GetMapping("/api/short-link/admin/v1/ops/lifecycle-alerts")
    public Result<Page<ShortLinkOpsLifecycleAlertRespDTO>> opsLifecycleAlerts(String gid,
                                                                               Integer expiringDays,
                                                                               Double quotaRiskThreshold,
                                                                               Long current,
                                                                               Long size) {
        return Results.success(shortLinkOpsInsightService.pageLifecycleAlerts(gid, expiringDays, quotaRiskThreshold, current, size));
    }
}
