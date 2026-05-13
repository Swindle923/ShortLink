package com.nageoffer.shortlink.project.console.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nageoffer.shortlink.project.common.biz.user.UserContext;
import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.common.convention.result.Results;
import com.nageoffer.shortlink.project.console.aspect.AdminAudit;
import com.nageoffer.shortlink.project.console.dao.entity.AdminAuditLogDO;
import com.nageoffer.shortlink.project.console.dto.req.ConsoleAuditLogPageReqDTO;
import com.nageoffer.shortlink.project.console.dto.req.ConsoleLinkPageReqDTO;
import com.nageoffer.shortlink.project.console.dto.req.ConsoleLoginReqDTO;
import com.nageoffer.shortlink.project.console.dto.req.ConsoleUserPageReqDTO;
import com.nageoffer.shortlink.project.console.dto.resp.ConsoleLinkRespDTO;
import com.nageoffer.shortlink.project.console.dto.resp.ConsoleLoginRespDTO;
import com.nageoffer.shortlink.project.console.dto.resp.ConsoleUserRespDTO;
import com.nageoffer.shortlink.project.console.service.ConsoleService;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkOpsLifecycleAlertRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkOpsOverviewRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkOpsRiskRespDTO;
import com.nageoffer.shortlink.project.service.ops.ShortLinkOpsInsightService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConsoleController {

    private final ConsoleService consoleService;
    private final ShortLinkOpsInsightService shortLinkOpsInsightService;

    @PostMapping("/api/short-link/console/v1/login")
    public Result<ConsoleLoginRespDTO> login(@RequestBody ConsoleLoginReqDTO req) {
        return Results.success(consoleService.login(req));
    }

    @PostMapping("/api/short-link/console/v1/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String username = request.getHeader("Username");
        String token = request.getHeader("Token");
        consoleService.logout(username, token);
        return Results.success();
    }

    @GetMapping("/api/short-link/console/v1/session")
    public Result<String> session() {
        return Results.success(UserContext.getUsername());
    }

    @GetMapping("/api/short-link/console/v1/users")
    public Result<IPage<ConsoleUserRespDTO>> pageUsers(ConsoleUserPageReqDTO req) {
        return Results.success(consoleService.pageUsers(req));
    }

    @AdminAudit(actionType = "FREEZE_USER", targetType = "USER")
    @PostMapping("/api/short-link/console/v1/users/freeze")
    public Result<Void> freezeUser(@RequestParam("username") String username) {
        consoleService.freezeUser(username);
        return Results.success();
    }

    @AdminAudit(actionType = "UNFREEZE_USER", targetType = "USER")
    @PostMapping("/api/short-link/console/v1/users/unfreeze")
    public Result<Void> unfreezeUser(@RequestParam("username") String username) {
        consoleService.unfreezeUser(username);
        return Results.success();
    }

    @AdminAudit(actionType = "UPDATE_USER_ROLE", targetType = "USER")
    @PostMapping("/api/short-link/console/v1/users/role")
    public Result<Void> updateUserRole(@RequestParam("username") String username,
                                       @RequestParam("role") String role) {
        consoleService.updateUserRole(username, role);
        return Results.success();
    }

    @GetMapping("/api/short-link/console/v1/links")
    public Result<IPage<ConsoleLinkRespDTO>> pageLinks(ConsoleLinkPageReqDTO req) {
        return Results.success(consoleService.pageLinks(req));
    }

    @AdminAudit(actionType = "DISABLE_LINK", targetType = "LINK")
    @PostMapping("/api/short-link/console/v1/links/disable")
    public Result<Void> disableLink(@RequestParam("fullShortUrl") String fullShortUrl,
                                     @RequestParam("gid") String gid) {
        consoleService.disableLink(fullShortUrl, gid);
        return Results.success();
    }

    @AdminAudit(actionType = "ENABLE_LINK", targetType = "LINK")
    @PostMapping("/api/short-link/console/v1/links/enable")
    public Result<Void> enableLink(@RequestParam("fullShortUrl") String fullShortUrl,
                                    @RequestParam("gid") String gid) {
        consoleService.enableLink(fullShortUrl, gid);
        return Results.success();
    }

    @GetMapping("/api/short-link/console/v1/ops/overview")
    public Result<ShortLinkOpsOverviewRespDTO> opsOverview(String gid,
                                                            Integer expiringDays,
                                                            Double quotaRiskThreshold) {
        return Results.success(shortLinkOpsInsightService.buildOverview(gid, expiringDays, quotaRiskThreshold));
    }

    @GetMapping("/api/short-link/console/v1/ops/high-risk")
    public Result<Page<ShortLinkOpsRiskRespDTO>> opsHighRisk(String gid, Long current, Long size) {
        return Results.success(shortLinkOpsInsightService.pageHighRisk(gid, current, size));
    }

    @GetMapping("/api/short-link/console/v1/ops/lifecycle-alerts")
    public Result<Page<ShortLinkOpsLifecycleAlertRespDTO>> opsAlerts(String gid,
                                                                      Integer expiringDays,
                                                                      Double quotaRiskThreshold,
                                                                      Long current,
                                                                      Long size) {
        return Results.success(shortLinkOpsInsightService.pageLifecycleAlerts(gid, expiringDays, quotaRiskThreshold, current, size));
    }

    @GetMapping("/api/short-link/console/v1/audit-logs")
    public Result<IPage<AdminAuditLogDO>> pageAuditLogs(ConsoleAuditLogPageReqDTO req) {
        return Results.success(consoleService.pageAuditLogs(req));
    }
}
