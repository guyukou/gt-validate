package com.prodaas.mapper;

import com.prodaas.model.GeetestTrail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by guyu on 2017/3/13.
 */
public interface GeetestTrailStatMapper {
    @Select("SELECT o.trail_id,o.deltax,o.trail FROM geetest_trail as o,geetest_trail_stat as p WHERE o.trail_id=p.trail_id AND p.bot_id=#{botId} AND o.deltax=#{deltaX}  ORDER BY o.deltax asc,p.success_count/(p.failure_count+p.success_count) DESC  LIMIT 1")
    GeetestTrail findOneByDeltaX(@Param("deltaX") int deltaX, @Param("botId") int botId);

    @Update("UPDATE geetest_trail_stat SET success_count = success_count + 1 WHERE trail_id = #{trailId} and bot_id = #{botId}")
    void updateSuccess(@Param("trailId") String trailId, @Param("botId") int botId);

    @Update("UPDATE geetest_trail_stat SET failure_count = failure_count + 1 WHERE trail_id = #{trailId} and bot_id = #{botId}")
    void updateFailure(@Param("trailId") String trailId, @Param("botId") int botId);
}
