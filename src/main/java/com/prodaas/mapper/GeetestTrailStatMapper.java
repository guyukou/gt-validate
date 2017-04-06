package com.prodaas.mapper;

import com.prodaas.model.GeetestTrail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by guyu on 2017/3/13.
 */
public interface GeetestTrailStatMapper {
    @Select("SELECT o.trail_id,o.trail FROM geetest_trail  o LEFT JOIN geetest_trail_stat  p ON o.trail_id = p.trail_id AND o.deltax=#{deltaX} WHERE  p.bot_id=#{botId}  AND p.status = 0  ORDER BY p.failure_continuity  LIMIT 20")
    List<GeetestTrail> findOneByDeltaX(@Param("deltaX") int deltaX, @Param("botId") int botId);

    @Update("UPDATE geetest_trail_stat SET success_count = success_count + 1, failure_continuity = 0 WHERE trail_id = #{trailId} and bot_id = #{botId}")
    void updateSuccess(@Param("trailId") String trailId, @Param("botId") int botId);

    void updateFailure(@Param("trailId") String trailId, @Param("botId") int botId);
}
