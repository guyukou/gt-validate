package com.prodaas.constant;

import org.apache.http.HttpHost;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * Created by guyu on 2017/3/14.
 */
public class Provinces {
    private static Set<String> provinces = new HashSet<>();
    private static Map<String, BlockingQueue<HttpHost>> proxies = new HashMap<>();
    private static Map<String, String> httpReferrers = new HashMap<>();
    private static Map<String, String> captchaUrls = new HashMap<>();
    private static Map<String, Integer> botIds = new HashMap<>();
    private static Map<String, Boolean> isUseProxy = new HashMap<>();
    public static boolean containsProvince(String province) {
        return provinces.contains(province);
    }


    public static String getCaptchaUrl(String province) {
        return captchaUrls.get(province);
    }

    public static String getReferrerHtml(String province) {
        return httpReferrers.get(province);
    }

    public static Integer getBotId(String province) {
        return botIds.get(province);
    }

    public static HttpHost getProxy(String province) {
        try {
            return proxies.get(province).take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static boolean isUseProxy(String province) {
        return isUseProxy.get(province);
    }

    public static void returnProxies(String province, HttpHost httpHost) {
        if (httpHost != null) {
            try {
                proxies.get(province).put(httpHost);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void putUseProxy(String province, Boolean useProxy) {
        isUseProxy.put(province, useProxy);
    }
    public static void putProxies(String province, BlockingQueue<HttpHost> queue) {
        proxies.put(province, queue);
    }

    public static void putHttpReferrer(String province, String httpReferrer) {
        httpReferrers.put(province, httpReferrer);
    }

    public static void putCaptchaUrl(String province, String captchaUrl) {
        captchaUrls.put(province, captchaUrl);
    }

    public static void putBotId(String province, Integer iBotId) {
        botIds.put(province, iBotId);
    }

    public static void putProvince(String province) {
        provinces.add(province);
    }

}
