package com.nageoffer.shortlink.admin.remote;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.dto.req.RecycleBinRecoverReqDTO;
import com.nageoffer.shortlink.admin.dto.req.RecycleBinRemoveReqDTO;
import com.nageoffer.shortlink.admin.dto.req.RecycleBinSaveReqDTO;
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

import java.util.List;

public interface ShortLinkActualRemoteService {

    Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam);

    Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam);

    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    Result<Page<ShortLinkPageRespDTO>> pageShortLink(String gid,
                                                     String orderTag,
                                                     Long current,
                                                     Long size);

    Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(List<String> requestParam);

    Result<String> getTitleByUrl(String url);

    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);

    Result<Page<ShortLinkPageRespDTO>> pageRecycleBinShortLink(List<String> gidList,
                                                               Long current,
                                                               Long size);

    void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam);

    void removeRecycleBin(RecycleBinRemoveReqDTO requestParam);

    Result<ShortLinkStatsRespDTO> oneShortLinkStats(String fullShortUrl,
                                                    String gid,
                                                    Integer enableStatus,
                                                    String startDate,
                                                    String endDate);

    Result<ShortLinkStatsRespDTO> groupShortLinkStats(String gid,
                                                      String startDate,
                                                      String endDate);

    Result<Page<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(String fullShortUrl,
                                                                               String gid,
                                                                               String startDate,
                                                                               String endDate,
                                                                               Integer enableStatus,
                                                                               Long current,
                                                                               Long size);

    Result<Page<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(String gid,
                                                                                    String startDate,
                                                                                    String endDate,
                                                                                    Long current,
                                                                                    Long size);

    Result<ShortLinkOpsOverviewRespDTO> opsOverview(String gid, Integer expiringDays, Double quotaRiskThreshold);

    Result<Page<ShortLinkOpsRiskRespDTO>> opsHighRisk(String gid, Long current, Long size);

    Result<Page<ShortLinkOpsLifecycleAlertRespDTO>> opsLifecycleAlerts(String gid,
                                                                       Integer expiringDays,
                                                                       Double quotaRiskThreshold,
                                                                       Long current,
                                                                       Long size);
}
