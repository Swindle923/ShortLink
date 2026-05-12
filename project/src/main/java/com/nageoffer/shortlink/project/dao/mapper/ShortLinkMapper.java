package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.apache.ibatis.annotations.Param;

public interface ShortLinkMapper extends BaseMapper<ShortLinkDO> {

    void incrementStats(@Param("gid") String gid,
                        @Param("fullShortUrl") String fullShortUrl,
                        @Param("totalPv") Integer totalPv,
                        @Param("totalUv") Integer totalUv,
                        @Param("totalUip") Integer totalUip);

    int incrementCurrentAccessCount(@Param("gid") String gid, @Param("fullShortUrl") String fullShortUrl);

    void disableByAccessLimit(@Param("gid") String gid, @Param("fullShortUrl") String fullShortUrl);

    IPage<ShortLinkDO> pageLink(ShortLinkPageReqDTO requestParam);

    IPage<ShortLinkDO> pageRecycleBinLink(ShortLinkRecycleBinPageReqDTO requestParam);
}
