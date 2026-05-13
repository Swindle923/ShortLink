package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.LinkAbTestDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface LinkAbTestMapper extends BaseMapper<LinkAbTestDO> {

    @Update("UPDATE t_link_ab_test SET hit_count = hit_count + 1, update_time = NOW() WHERE id = #{id} AND del_flag = 0")
    int incrementHitCount(@Param("id") Long id);

    @Select("SELECT variant_key AS variantKey, " +
            "       COUNT(*) AS pv, " +
            "       COUNT(DISTINCT user) AS uv, " +
            "       COUNT(DISTINCT ip) AS uip " +
            "FROM t_link_access_logs " +
            "WHERE full_short_url = #{fullShortUrl} " +
            "  AND variant_key IS NOT NULL " +
            "  AND del_flag = 0 " +
            "GROUP BY variant_key")
    List<Map<String, Object>> aggregateVariantStats(@Param("fullShortUrl") String fullShortUrl);
}
