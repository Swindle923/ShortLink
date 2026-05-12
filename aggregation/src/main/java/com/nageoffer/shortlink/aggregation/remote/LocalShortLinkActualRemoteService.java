package com.nageoffer.shortlink.aggregation.remote;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.dto.req.RecycleBinRecoverReqDTO;
import com.nageoffer.shortlink.admin.dto.req.RecycleBinRemoveReqDTO;
import com.nageoffer.shortlink.admin.dto.req.RecycleBinSaveReqDTO;
import com.nageoffer.shortlink.admin.remote.ShortLinkActualRemoteService;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkBatchCreateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkBatchCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkOpsLifecycleAlertRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkOpsOverviewRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkOpsRiskRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkStatsRespDTO;
import com.nageoffer.shortlink.aggregation.service.ShortLinkOpsInsightService;
import com.nageoffer.shortlink.project.service.RecycleBinService;
import com.nageoffer.shortlink.project.service.ShortLinkService;
import com.nageoffer.shortlink.project.service.ShortLinkStatsService;
import com.nageoffer.shortlink.project.service.UrlTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Primary
@Service
@RequiredArgsConstructor
public class LocalShortLinkActualRemoteService implements ShortLinkActualRemoteService {

    private final ShortLinkService shortLinkService;
    private final RecycleBinService recycleBinService;
    private final ShortLinkStatsService shortLinkStatsService;
    private final UrlTitleService urlTitleService;
    private final ShortLinkOpsInsightService shortLinkOpsInsightService;

    @Override
    public Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {
        com.nageoffer.shortlink.project.dto.req.ShortLinkCreateReqDTO actualRequest =
                convert(requestParam, com.nageoffer.shortlink.project.dto.req.ShortLinkCreateReqDTO.class);
        com.nageoffer.shortlink.project.dto.resp.ShortLinkCreateRespDTO actualResponse =
                shortLinkService.createShortLink(actualRequest);
        return Results.success(convert(actualResponse, ShortLinkCreateRespDTO.class));
    }

    @Override
    public Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam) {
        com.nageoffer.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO actualRequest =
                convert(requestParam, com.nageoffer.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO.class);
        com.nageoffer.shortlink.project.dto.resp.ShortLinkBatchCreateRespDTO actualResponse =
                shortLinkService.batchCreateShortLink(actualRequest);
        return Results.success(convert(actualResponse, ShortLinkBatchCreateRespDTO.class));
    }

    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        com.nageoffer.shortlink.project.dto.req.ShortLinkUpdateReqDTO actualRequest =
                convert(requestParam, com.nageoffer.shortlink.project.dto.req.ShortLinkUpdateReqDTO.class);
        shortLinkService.updateShortLink(actualRequest);
    }

    @Override
    public Result<Page<ShortLinkPageRespDTO>> pageShortLink(String gid, String orderTag, Long current, Long size) {
        com.nageoffer.shortlink.project.dto.req.ShortLinkPageReqDTO actualRequest =
                new com.nageoffer.shortlink.project.dto.req.ShortLinkPageReqDTO();
        actualRequest.setGid(gid);
        actualRequest.setOrderTag(orderTag);
        actualRequest.setCurrent(current);
        actualRequest.setSize(size);
        IPage<com.nageoffer.shortlink.project.dto.resp.ShortLinkPageRespDTO> actualPage =
                shortLinkService.pageShortLink(actualRequest);
        return Results.success(convertPage(actualPage, ShortLinkPageRespDTO.class));
    }

    @Override
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(List<String> requestParam) {
        List<com.nageoffer.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO> actualResponse =
                shortLinkService.listGroupShortLinkCount(requestParam);
        return Results.success(convertList(actualResponse, ShortLinkGroupCountQueryRespDTO.class));
    }

    @Override
    public Result<String> getTitleByUrl(String url) {
        return Results.success(urlTitleService.getTitleByUrl(url));
    }

    @Override
    public void saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        com.nageoffer.shortlink.project.dto.req.RecycleBinSaveReqDTO actualRequest =
                convert(requestParam, com.nageoffer.shortlink.project.dto.req.RecycleBinSaveReqDTO.class);
        recycleBinService.saveRecycleBin(actualRequest);
    }

    @Override
    public Result<Page<ShortLinkPageRespDTO>> pageRecycleBinShortLink(List<String> gidList, Long current, Long size) {
        com.nageoffer.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO actualRequest =
                new com.nageoffer.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO();
        actualRequest.setGidList(gidList);
        actualRequest.setCurrent(current);
        actualRequest.setSize(size);
        IPage<com.nageoffer.shortlink.project.dto.resp.ShortLinkPageRespDTO> actualPage =
                recycleBinService.pageRecycleBinShortLink(actualRequest);
        return Results.success(convertPage(actualPage, ShortLinkPageRespDTO.class));
    }

    @Override
    public void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        com.nageoffer.shortlink.project.dto.req.RecycleBinRecoverReqDTO actualRequest =
                convert(requestParam, com.nageoffer.shortlink.project.dto.req.RecycleBinRecoverReqDTO.class);
        recycleBinService.recoverRecycleBin(actualRequest);
    }

    @Override
    public void removeRecycleBin(RecycleBinRemoveReqDTO requestParam) {
        com.nageoffer.shortlink.project.dto.req.RecycleBinRemoveReqDTO actualRequest =
                convert(requestParam, com.nageoffer.shortlink.project.dto.req.RecycleBinRemoveReqDTO.class);
        recycleBinService.removeRecycleBin(actualRequest);
    }

    @Override
    public Result<ShortLinkStatsRespDTO> oneShortLinkStats(String fullShortUrl, String gid, Integer enableStatus, String startDate, String endDate) {
        com.nageoffer.shortlink.project.dto.req.ShortLinkStatsReqDTO actualRequest =
                new com.nageoffer.shortlink.project.dto.req.ShortLinkStatsReqDTO();
        actualRequest.setFullShortUrl(fullShortUrl);
        actualRequest.setGid(gid);
        actualRequest.setEnableStatus(enableStatus);
        actualRequest.setStartDate(startDate);
        actualRequest.setEndDate(endDate);
        com.nageoffer.shortlink.project.dto.resp.ShortLinkStatsRespDTO actualResponse =
                shortLinkStatsService.oneShortLinkStats(actualRequest);
        return Results.success(convert(actualResponse, ShortLinkStatsRespDTO.class));
    }

    @Override
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(String gid, String startDate, String endDate) {
        com.nageoffer.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO actualRequest =
                new com.nageoffer.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO();
        actualRequest.setGid(gid);
        actualRequest.setStartDate(startDate);
        actualRequest.setEndDate(endDate);
        com.nageoffer.shortlink.project.dto.resp.ShortLinkStatsRespDTO actualResponse =
                shortLinkStatsService.groupShortLinkStats(actualRequest);
        return Results.success(convert(actualResponse, ShortLinkStatsRespDTO.class));
    }

    @Override
    public Result<Page<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(
            String fullShortUrl,
            String gid,
            String startDate,
            String endDate,
            Integer enableStatus,
            Long current,
            Long size) {
        com.nageoffer.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO actualRequest =
                new com.nageoffer.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO();
        actualRequest.setFullShortUrl(fullShortUrl);
        actualRequest.setGid(gid);
        actualRequest.setStartDate(startDate);
        actualRequest.setEndDate(endDate);
        actualRequest.setEnableStatus(enableStatus);
        actualRequest.setCurrent(current);
        actualRequest.setSize(size);
        IPage<com.nageoffer.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO> actualPage =
                shortLinkStatsService.shortLinkStatsAccessRecord(actualRequest);
        return Results.success(convertPage(actualPage, ShortLinkStatsAccessRecordRespDTO.class));
    }

    @Override
    public Result<Page<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(String gid, String startDate, String endDate, Long current, Long size) {
        com.nageoffer.shortlink.project.dto.req.ShortLinkGroupStatsAccessRecordReqDTO actualRequest =
                new com.nageoffer.shortlink.project.dto.req.ShortLinkGroupStatsAccessRecordReqDTO();
        actualRequest.setGid(gid);
        actualRequest.setStartDate(startDate);
        actualRequest.setEndDate(endDate);
        actualRequest.setCurrent(current);
        actualRequest.setSize(size);
        IPage<com.nageoffer.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO> actualPage =
                shortLinkStatsService.groupShortLinkStatsAccessRecord(actualRequest);
        return Results.success(convertPage(actualPage, ShortLinkStatsAccessRecordRespDTO.class));
    }

    @Override
    public Result<ShortLinkOpsOverviewRespDTO> opsOverview(String gid, Integer expiringDays, Double quotaRiskThreshold) {
        return Results.success(shortLinkOpsInsightService.buildOverview(gid, expiringDays, quotaRiskThreshold));
    }

    @Override
    public Result<Page<ShortLinkOpsRiskRespDTO>> opsHighRisk(String gid, Long current, Long size) {
        return Results.success(shortLinkOpsInsightService.pageHighRisk(gid, current, size));
    }

    @Override
    public Result<Page<ShortLinkOpsLifecycleAlertRespDTO>> opsLifecycleAlerts(String gid,
                                                                               Integer expiringDays,
                                                                               Double quotaRiskThreshold,
                                                                               Long current,
                                                                               Long size) {
        return Results.success(shortLinkOpsInsightService.pageLifecycleAlerts(gid, expiringDays, quotaRiskThreshold, current, size));
    }

    private <T> T convert(Object source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(source), targetType);
    }

    private <T> List<T> convertList(Object source, Class<T> targetType) {
        if (source == null) {
            return Collections.emptyList();
        }
        return JSON.parseArray(JSON.toJSONString(source), targetType);
    }

    private <S, T> Page<T> convertPage(IPage<S> source, Class<T> targetType) {
        Page<T> target = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        target.setRecords(convertList(source.getRecords(), targetType));
        return target;
    }
}
