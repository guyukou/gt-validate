package com.prodaas.model;

import java.util.Date;
import java.util.UUID;

/**
 * Created by lfc 20170220
 * 轨迹破解日志
 */
public class GeetestTrailLog {

    private String Id;
    private String trailId;
    private String msg;
    private Integer botId;
    private Date createTime;



    public GeetestTrailLog() {
    }

    public GeetestTrailLog(String trailId, Integer botId, String msg,Date createTime) {
        this.Id =  UUID.randomUUID().toString().replaceAll("-","");
        this.trailId = trailId;
        this.botId = botId;
        this.msg = msg;
        this.createTime = createTime;
    }


    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
