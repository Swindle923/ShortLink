package com.nageoffer.shortlink.project.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsRespDTO {

    private Integer pv;

    private Integer uv;

    private Integer uip;

    private List<ShortLinkStatsAccessDailyRespDTO> daily;

    private List<ShortLinkStatsLocaleCNRespDTO> localeCnStats;

    private List<Integer> hourStats;

    private List<ShortLinkStatsTopIpRespDTO> topIpStats;

    private List<Integer> weekdayStats;

    private List<ShortLinkStatsBrowserRespDTO> browserStats;

    private List<ShortLinkStatsOsRespDTO> osStats;

    private List<ShortLinkStatsUvRespDTO> uvTypeStats;

    private List<ShortLinkStatsDeviceRespDTO> deviceStats;

    private List<ShortLinkStatsNetworkRespDTO> networkStats;
}
