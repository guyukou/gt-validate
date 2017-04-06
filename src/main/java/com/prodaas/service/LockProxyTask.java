package com.prodaas.service;

import com.prodaas.constant.Provinces;
import org.apache.http.HttpHost;

import java.util.concurrent.TimeUnit;

/**
 * Created by guyu on 2017/4/6.
 */
public class LockProxyTask implements Runnable {
    private String province;
    private HttpHost proxy;
    private int sleepTime;

    public LockProxyTask(String province, HttpHost proxy,int sleepTime) {
        this.province = province;
        this.proxy = proxy;
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            Provinces.returnProxies(province, proxy);
        }
    }
}
