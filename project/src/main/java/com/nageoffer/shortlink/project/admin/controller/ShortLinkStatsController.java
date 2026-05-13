package com.nageoffer.shortlink.project.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.common.convention.result.Results;
import com.nageoffer.shortlink.project.dao.entity.LinkAbTestDO;
import com.nageoffer.shortlink.project.dao.mapper.LinkAbTestMapper;
import com.nageoffer.shortlink.project.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkAbVariantStatsRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkStatsRespDTO;
import com.nageoffer.shortlink.project.service.ShortLinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController(value = "shortLinkStatsControllerByAdmin")
@RequiredArgsConstructor
public class ShortLinkStatsController {

    private final ShortLinkStatsService shortLinkStatsService;
    private final LinkAbTestMapper linkAbTestMapper;

    @GetMapping("/api/short-link/admin/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return Results.success(shortLinkStatsService.oneShortLinkStats(requestParam));
    }

    @GetMapping("/api/short-link/admin/v1/stats/group")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam) {
        return Results.success(shortLinkStatsService.groupShortLinkStats(requestParam));
    }

    @GetMapping("/api/short-link/admin/v1/stats/access-record")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        return Results.success(shortLinkStatsService.shortLinkStatsAccessRecord(requestParam));
    }

    @GetMapping("/api/short-link/admin/v1/stats/access-record/group")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam) {
        return Results.success(shortLinkStatsService.groupShortLinkStatsAccessRecord(requestParam));
    }

    @GetMapping("/api/short-link/admin/v1/stats/ab")
    public Result<List<ShortLinkAbVariantStatsRespDTO>> abStats(@RequestParam("fullShortUrl") String fullShortUrl) {
        List<LinkAbTestDO> variants = linkAbTestMapper.selectList(
                Wrappers.lambdaQuery(LinkAbTestDO.class)
                        .eq(LinkAbTestDO::getFullShortUrl, fullShortUrl)
                        .eq(LinkAbTestDO::getDelFlag, 0)
                        .orderByAsc(LinkAbTestDO::getVariantKey)
        );
        if (variants.isEmpty()) {
            return Results.success(new ArrayList<>());
        }
        Map<String, Map<String, Object>> aggMap = new HashMap<>();
        for (Map<String, Object> row : linkAbTestMapper.aggregateVariantStats(fullShortUrl)) {
            aggMap.put(String.valueOf(row.get("variantKey")), row);
        }
        long totalPv = aggMap.values().stream()
                .mapToLong(m -> ((Number) m.getOrDefault("pv", 0L)).longValue())
                .sum();
        List<ShortLinkAbVariantStatsRespDTO> result = new ArrayList<>();
        for (LinkAbTestDO v : variants) {
            ShortLinkAbVariantStatsRespDTO dto = new ShortLinkAbVariantStatsRespDTO();
            dto.setVariantKey(v.getVariantKey());
            dto.setTargetUrl(v.getTargetUrl());
            dto.setWeight(v.getWeight());
            dto.setHitCount(v.getHitCount());
            Map<String, Object> agg = aggMap.get(v.getVariantKey());
            int pv = agg == null ? 0 : ((Number) agg.getOrDefault("pv", 0)).intValue();
            int uv = agg == null ? 0 : ((Number) agg.getOrDefault("uv", 0)).intValue();
            int uip = agg == null ? 0 : ((Number) agg.getOrDefault("uip", 0)).intValue();
            dto.setPv(pv);
            dto.setUv(uv);
            dto.setUip(uip);
            dto.setPvRatio(totalPv == 0 ? 0.0 : Math.round((double) pv / totalPv * 10000.0) / 100.0);
            result.add(dto);
        }
        return Results.success(result);
    }
}
