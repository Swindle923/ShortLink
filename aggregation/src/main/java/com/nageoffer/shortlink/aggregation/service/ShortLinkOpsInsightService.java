package com.nageoffer.shortlink.aggregation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkOpsLifecycleAlertRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkOpsOverviewRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkOpsRiskRespDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.nageoffer.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShortLinkOpsInsightService {

    private static final int FETCH_PAGE_SIZE = 200;

    private final ShortLinkService shortLinkService;

    public ShortLinkOpsOverviewRespDTO buildOverview(String gid, Integer expiringDays, Double quotaRiskThreshold) {
        int effectiveExpiringDays = expiringDays == null || expiringDays <= 0 ? 7 : expiringDays;
        double effectiveQuotaRiskThreshold = quotaRiskThreshold == null || quotaRiskThreshold <= 0 ? 0.8D : quotaRiskThreshold;
        List<ShortLinkPageRespDTO> allLinks = fetchAllLinksByGid(gid);
        Date now = new Date();
        int linkCount = allLinks.size();
        int activeCount = 0;
        int expiringSoonCount = 0;
        int quotaRiskCount = 0;
        int exhaustedCount = 0;
        int highRiskCount = 0;
        long todayPv = 0L;
        long totalPv = 0L;
        for (ShortLinkPageRespDTO each : allLinks) {
            RiskContext ctx = buildRiskContext(each, now);
            if (Objects.equals(each.getEnableStatus(), 0) && !ctx.expired) {
                activeCount++;
            }
            if (!ctx.expired && ctx.daysToExpire != null && ctx.daysToExpire <= effectiveExpiringDays) {
                expiringSoonCount++;
            }
            if (ctx.quotaUsageRate != null && ctx.quotaUsageRate >= effectiveQuotaRiskThreshold) {
                quotaRiskCount++;
            }
            if (ctx.quotaUsageRate != null && ctx.quotaUsageRate >= 1D) {
                exhaustedCount++;
            }
            if (ctx.riskScore >= 70) {
                highRiskCount++;
            }
            todayPv += safeInt(each.getTodayPv());
            totalPv += safeInt(each.getTotalPv());
        }
        ShortLinkOpsOverviewRespDTO result = new ShortLinkOpsOverviewRespDTO();
        result.setLinkCount(linkCount);
        result.setActiveCount(activeCount);
        result.setExpiringSoonCount(expiringSoonCount);
        result.setQuotaRiskCount(quotaRiskCount);
        result.setExhaustedCount(exhaustedCount);
        result.setHighRiskCount(highRiskCount);
        result.setHealthyRate(linkCount == 0 ? 100D : round2(100D * (linkCount - highRiskCount) / linkCount));
        result.setTodayPv(todayPv);
        result.setTotalPv(totalPv);
        return result;
    }

    public Page<ShortLinkOpsRiskRespDTO> pageHighRisk(String gid, Long current, Long size) {
        long effectiveCurrent = current == null || current < 1 ? 1 : current;
        long effectiveSize = size == null || size < 1 ? 10 : size;
        Date now = new Date();
        List<ShortLinkOpsRiskRespDTO> riskList = fetchAllLinksByGid(gid).stream()
                .map(each -> toRiskResp(each, buildRiskContext(each, now)))
                .filter(each -> each.getRiskScore() >= 40)
                .sorted(Comparator.comparing(ShortLinkOpsRiskRespDTO::getRiskScore).reversed()
                        .thenComparing(ShortLinkOpsRiskRespDTO::getTodayPv, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        return toPage(riskList, effectiveCurrent, effectiveSize);
    }

    public Page<ShortLinkOpsLifecycleAlertRespDTO> pageLifecycleAlerts(String gid,
                                                                       Integer expiringDays,
                                                                       Double quotaRiskThreshold,
                                                                       Long current,
                                                                       Long size) {
        int effectiveExpiringDays = expiringDays == null || expiringDays <= 0 ? 7 : expiringDays;
        double effectiveQuotaRiskThreshold = quotaRiskThreshold == null || quotaRiskThreshold <= 0 ? 0.8D : quotaRiskThreshold;
        long effectiveCurrent = current == null || current < 1 ? 1 : current;
        long effectiveSize = size == null || size < 1 ? 10 : size;
        Date now = new Date();
        List<ShortLinkOpsLifecycleAlertRespDTO> alerts = new ArrayList<>();
        for (ShortLinkPageRespDTO each : fetchAllLinksByGid(gid)) {
            RiskContext ctx = buildRiskContext(each, now);
            if (ctx.expired) {
                alerts.add(buildAlert(each, "expiring", "短链已过期，建议恢复或更新有效期", ctx));
                continue;
            }
            if (ctx.daysToExpire != null && ctx.daysToExpire <= effectiveExpiringDays) {
                alerts.add(buildAlert(each, "expiring",
                        String.format("短链将在 %d 天内到期，建议提前续期", ctx.daysToExpire), ctx));
            }
            if (ctx.quotaUsageRate != null && ctx.quotaUsageRate >= 1D) {
                alerts.add(buildAlert(each, "exhausted", "配额已耗尽，短链可能已不可访问", ctx));
            } else if (ctx.quotaUsageRate != null && ctx.quotaUsageRate >= effectiveQuotaRiskThreshold) {
                alerts.add(buildAlert(each, "quota",
                        String.format("配额使用率 %.2f%%，建议提升额度或回收低效流量", ctx.quotaUsageRate * 100), ctx));
            }
            if (!Objects.equals(each.getEnableStatus(), 0)) {
                alerts.add(buildAlert(each, "disabled", "短链已禁用，建议确认是否需要恢复", ctx));
            }
        }
        alerts.sort(Comparator.comparing(this::alertRank).thenComparing(ShortLinkOpsLifecycleAlertRespDTO::getDaysToExpire,
                Comparator.nullsLast(Long::compareTo)));
        return toPage(alerts, effectiveCurrent, effectiveSize);
    }

    private int alertRank(ShortLinkOpsLifecycleAlertRespDTO alert) {
        return switch (alert.getAlertType()) {
            case "exhausted" -> 1;
            case "expiring" -> 2;
            case "quota" -> 3;
            default -> 4;
        };
    }

    private ShortLinkOpsLifecycleAlertRespDTO buildAlert(ShortLinkPageRespDTO source,
                                                         String type,
                                                         String message,
                                                         RiskContext context) {
        ShortLinkOpsLifecycleAlertRespDTO resp = new ShortLinkOpsLifecycleAlertRespDTO();
        resp.setFullShortUrl(source.getFullShortUrl());
        resp.setDescribe(source.getDescribe());
        resp.setAlertType(type);
        resp.setAlertMessage(message);
        resp.setQuotaUsageRate(context.quotaUsageRate == null ? null : round2(context.quotaUsageRate));
        resp.setDaysToExpire(context.daysToExpire);
        return resp;
    }

    private ShortLinkOpsRiskRespDTO toRiskResp(ShortLinkPageRespDTO source, RiskContext context) {
        ShortLinkOpsRiskRespDTO resp = new ShortLinkOpsRiskRespDTO();
        resp.setFullShortUrl(source.getFullShortUrl());
        resp.setDescribe(source.getDescribe());
        resp.setRiskScore(context.riskScore);
        resp.setRiskLevel(context.riskScore >= 70 ? "high" : (context.riskScore >= 40 ? "medium" : "low"));
        resp.setRiskReason(String.join("；", context.reasons));
        resp.setTodayPv(safeInt(source.getTodayPv()));
        resp.setTotalPv(safeInt(source.getTotalPv()));
        resp.setQuotaUsageRate(context.quotaUsageRate == null ? null : round2(context.quotaUsageRate));
        resp.setDaysToExpire(context.daysToExpire);
        resp.setVisitSpikeRatio(round2(context.visitSpikeRatio));
        return resp;
    }

    private RiskContext buildRiskContext(ShortLinkPageRespDTO each, Date now) {
        RiskContext context = new RiskContext();
        context.reasons = new ArrayList<>();
        context.riskScore = 0;
        if (!Objects.equals(each.getEnableStatus(), 0)) {
            context.riskScore += 35;
            context.reasons.add("当前状态禁用");
        }
        context.expired = false;
        if (Objects.equals(each.getValidDateType(), 1) && each.getValidDate() != null) {
            long diff = each.getValidDate().getTime() - now.getTime();
            long days = TimeUnit.MILLISECONDS.toDays(Math.max(diff, 0));
            context.daysToExpire = days;
            if (diff < 0) {
                context.expired = true;
                context.daysToExpire = 0L;
                context.riskScore += 40;
                context.reasons.add("有效期已过期");
            } else if (days <= 3) {
                context.riskScore += 25;
                context.reasons.add("3天内到期");
            } else if (days <= 7) {
                context.riskScore += 15;
                context.reasons.add("7天内到期");
            }
        }
        context.quotaUsageRate = calcQuotaUsage(each.getCurrentAccessCount(), each.getMaxAccessCount());
        if (context.quotaUsageRate != null) {
            if (context.quotaUsageRate >= 1D) {
                context.riskScore += 35;
                context.reasons.add("配额已耗尽");
            } else if (context.quotaUsageRate >= 0.9D) {
                context.riskScore += 25;
                context.reasons.add("配额使用率超过90%");
            } else if (context.quotaUsageRate >= 0.8D) {
                context.riskScore += 15;
                context.reasons.add("配额使用率超过80%");
            }
        }
        context.visitSpikeRatio = calcSpikeRatio(each);
        if (context.visitSpikeRatio >= 4 && safeInt(each.getTodayPv()) >= 50) {
            context.riskScore += 20;
            context.reasons.add("访问突增明显");
        } else if (context.visitSpikeRatio >= 2.5 && safeInt(each.getTodayPv()) >= 20) {
            context.riskScore += 10;
            context.reasons.add("访问增速异常");
        }
        context.riskScore = Math.min(context.riskScore, 100);
        if (context.reasons.isEmpty()) {
            context.reasons.add("整体平稳");
        }
        return context;
    }

    private double calcSpikeRatio(ShortLinkPageRespDTO each) {
        int todayPv = safeInt(each.getTodayPv());
        int totalPv = safeInt(each.getTotalPv());
        Date createTime = each.getCreateTime();
        if (createTime == null) {
            return todayPv <= 0 ? 0 : todayPv;
        }
        long liveDays = Math.max(TimeUnit.MILLISECONDS.toDays(new Date().getTime() - createTime.getTime()) + 1, 1);
        double historyAvg = (double) Math.max(totalPv - todayPv, 0) / Math.max(liveDays - 1, 1);
        return todayPv / Math.max(historyAvg, 1D);
    }

    private Double calcQuotaUsage(Integer currentAccessCount, Integer maxAccessCount) {
        if (maxAccessCount == null || maxAccessCount <= 0) {
            return null;
        }
        return Math.max(safeInt(currentAccessCount), 0) * 1D / maxAccessCount;
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private List<ShortLinkPageRespDTO> fetchAllLinksByGid(String gid) {
        List<ShortLinkPageRespDTO> result = new ArrayList<>();
        long current = 1L;
        while (true) {
            ShortLinkPageReqDTO req = new ShortLinkPageReqDTO();
            req.setGid(gid);
            req.setCurrent(current);
            req.setSize((long) FETCH_PAGE_SIZE);
            IPage<ShortLinkPageRespDTO> page = shortLinkService.pageShortLink(req);
            if (page == null || page.getRecords() == null || page.getRecords().isEmpty()) {
                break;
            }
            result.addAll(page.getRecords());
            if (current * FETCH_PAGE_SIZE >= page.getTotal()) {
                break;
            }
            current++;
        }
        return result;
    }

    private <T> Page<T> toPage(List<T> source, long current, long size) {
        Page<T> target = new Page<>(current, size, source.size());
        int from = (int) Math.max((current - 1) * size, 0);
        int to = (int) Math.min(from + size, source.size());
        if (from >= source.size()) {
            target.setRecords(List.of());
            return target;
        }
        target.setRecords(source.subList(from, to));
        return target;
    }

    private double round2(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private static class RiskContext {
        private int riskScore;
        private List<String> reasons;
        private boolean expired;
        private Long daysToExpire;
        private Double quotaUsageRate;
        private double visitSpikeRatio;
    }
}
