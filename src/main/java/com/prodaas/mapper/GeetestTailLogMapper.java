package com.prodaas.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * Created by guyu on 2017/3/15.
 */
public interface GeetestTailLogMapper {

    @Insert("INSERT INTO geetest_trail_log (trail_id, bot_id, create_time, msg, id) VALUES (#{trailId}, #{botId}, NOW(), #{msg}, REPLACE(uuid(),'-',''))")
    void insertLog(@Param("trailId") String trailId, @Param("botId")Integer botId, @Param("msg")String msg);
}

