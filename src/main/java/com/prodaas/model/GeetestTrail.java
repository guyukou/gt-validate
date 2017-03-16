package com.prodaas.model;


import java.util.Date;

/**
 * Created by  yuananyun on 2016/12/13.
 */
public class GeetestTrail {

    private String trailId;
    private Integer deltaX;
    private String trail;

    private Integer successCount;
    private Integer failureCount;

    private String createUser;
    private Date createTime;
    private Date updateTime;
    private String trailProvince;


    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public GeetestTrail() {
    }

    public GeetestTrail(String trailId, Integer deltaX, String trail) {
        this.trailId = trailId;
        this.deltaX = deltaX;
        this.trail = trail;
        failureCount = 0;
        successCount = 1;
    }

    // 20170220,for collect trail
    public GeetestTrail(String trailId, Integer deltaX, String trail, String createUser, Date createTime, Date updateTime, String trailProvince) {
        this.trailId = trailId;
        this.deltaX = deltaX;
        this.trail = trail;
        failureCount = 0;
        successCount = 1;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.trailProvince = trailProvince;
    }





    public String getTrailId() {
        return trailId;
    }

    public void setTrailId(String trailId) {
        this.trailId = trailId;
    }

    public Integer getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(Integer deltaX) {
        this.deltaX = deltaX;
    }

    public String getTrail() {
        return trail;
    }

    public void setTrail(String trail) {
        this.trail = trail;
    }


    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getTrailProvince() {
        return trailProvince;
    }

    public void setTrailProvince(String trailProvince) {
        this.trailProvince = trailProvince;
    }
}
