package com.prodaas.model;

import java.util.Date;

/**
 * Created by lfc 20170221
 * 轨迹破解统计
 */
public class GeetestTrailStat {

    private String trailId;
    private Integer botId;
    private Integer successCount;
    private Integer failureCount;
    private Date updateTime;



    public GeetestTrailStat() {
    }

    public GeetestTrailStat(String trailId, Integer botId, Integer successCount,Integer failureCount, Date updateTime) {
        this.trailId = trailId;
        this.botId = botId;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.updateTime = updateTime;
    }


    public String getTrailId() {
        return trailId;
    }

    public void setTrailId(String trailId) {
        this.trailId = trailId;
    }

    public Integer getBotId() {
        return botId;
    }

    public void setBotId(Integer botId) {
        this.botId = botId;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }


    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
