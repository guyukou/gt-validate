package com.prodaas.service;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;
import com.prodaas.constant.Provinces;
import com.prodaas.exception.DeltaXResolveFailException;
import com.prodaas.exception.IPLimitException;
import com.prodaas.mapper.GeetestTailLogMapper;
import com.prodaas.mapper.GeetestTrailStatMapper;
import com.prodaas.model.GeetestTrail;
import com.prodaas.util.ImageUtils;
import com.prodaas.util.JSEngine;
import com.prodaas.util.TrailGen;
import com.prodaas.util.TrailWeaver;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CrawlService {
    private static final Logger logger = LoggerFactory.getLogger(CrawlService.class);
    private static String PRE_FIX = "http://static.geetest.com/";
    private static List<String> POSITIONS = Arrays.asList("-157px -58px", "-145px -58px", "-265px -58px", "-277px -58px", "-181px -58px", "-169px -58px", "-241px -58px", "-253px -58px", "-109px -58px", "-97px -58px", "-289px -58px", "-301px -58px", "-85px -58px", "-73px -58px", "-25px -58px", "-37px -58px", "-13px -58px", "-1px -58px", "-121px -58px", "-133px -58px", "-61px -58px", "-49px -58px", "-217px -58px", "-229px -58px", "-205px -58px", "-193px -58px", "-145px 0px", "-157px 0px", "-277px 0px", "-265px 0px", "-169px 0px", "-181px 0px", "-253px 0px", "-241px 0px", "-97px 0px", "-109px 0px", "-301px 0px", "-289px 0px", "-73px 0px", "-85px 0px", "-37px 0px", "-25px 0px", "-1px 0px", "-13px 0px", "-133px 0px", "-121px 0px", "-49px 0px", "-61px 0px", "-229px 0px", "-217px 0px", "-193px 0px", "-205px 0px");
    private static Random random = new Random();

    private static Pattern GT_BODY_PTN = Pattern.compile("geetest_\\d+?\\((.+)\\)");
    private static Pattern LAST_NUM_PTN = Pattern.compile("(\\d+)\\]");

    private ExecutorService exec = Executors.newCachedThreadPool();
    @Value("${try_count}")
    private int tryCount;
    @Value("${proxy_timeout}")
    private int proxyTimeout;
    @Value("${crack_interval}")
    private int crackInterval;

    @Autowired
    private GeetestTrailStatMapper geetestTrailStatMapper;
    @Autowired
    private GeetestTailLogMapper geetestTailLogMapper;

    public DBObject getGCV(String province) {
        int count = 0;
        DBObject result = null;
        while (++count <= tryCount) {
            HttpHost proxy;
            if (Provinces.isUseProxy(province)) {
                proxy = Provinces.getProxy(province);
            } else {
                proxy = null;
            }

            boolean hostAvailable = true;
            boolean ipLimit = false;
            try {
                result = crack(proxy, province);
            } catch (HttpHostConnectException | ConnectTimeoutException | NoRouteToHostException | SocketTimeoutException e) {
                hostAvailable = false;
                logger.error("connect exception", e);
            } catch (JSONParseException e) {
                hostAvailable = false;
                logger.error("unknown exception", e);
            } catch (IPLimitException e) {
                ipLimit = true;
                logger.error("ip limit", e);
            } catch (Exception e) {
                logger.error("crack error", e);
            } finally {
                if (hostAvailable) {
                    Provinces.returnProxies(province, proxy);
                } else {
                    int sleepTime = ipLimit ? 3600 : 30;
                    exec.execute(new LockProxyTask(province, proxy, sleepTime));
                }
            }
            if (result != null) {
                break;
            }
        }
        if (result == null) {
            result = new BasicDBObject();
            result.put("status", "error");
            result.put("msg", "try " + count + ",failed");
        }
        result.put("try", count);
        return result;
    }

    private DBObject crack(HttpHost proxy, String province) throws Exception {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            RequestConfig.Builder builder = RequestConfig.custom()
                    .setConnectTimeout(proxyTimeout).setSocketTimeout(proxyTimeout);
            if (proxy != null) {
                builder.setProxy(proxy);
            }
            RequestConfig config = builder.build();
            long threadId = Thread.currentThread().getId();
            // 1. get first challenge
            HttpGet httpGet = new HttpGet(Provinces.getCaptchaUrl(province).replace("{timestamp}", System.currentTimeMillis() + ""));
            httpGet.setConfig(config);
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
            httpGet.setHeader("Referer", Provinces.getReferrerHtml(province));
            long httpSpan = System.currentTimeMillis();
            CloseableHttpResponse response = httpClient.execute(httpGet);
            httpSpan = System.currentTimeMillis() - httpSpan;
            String output;
            try {
                HttpEntity entity = response.getEntity();
                output = EntityUtils.toString(entity);
            } finally {
                response.close();
            }
            DBObject parse = (DBObject) JSON.parse(output);
            logger.trace(parse.toString());

            // 2 get second challenge and etc.
            String gt = (String) parse.get("gt");
            String challenge = (String) parse.get("challenge");
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("api.geetest.com")
                    .setPath("get.php")
                    .setParameter("gt", gt)
                    .setParameter("challenge", challenge)
                    .setParameter("product", "popup")
                    .setParameter("offline", "false")
                    .setParameter("protocol", "")
                    .setParameter("path", "/static/js/geetest.5.10.10.js")
                    .setParameter("type", "slide")
                    .setParameter("callback", "geetest_" + System.currentTimeMillis())
                    .build();
            httpGet = new HttpGet(uri);
            httpGet.setConfig(config);
            httpGet.setHeader("Referer", Provinces.getReferrerHtml(province));
            httpSpan -= System.currentTimeMillis();
            response = httpClient.execute(httpGet);
            httpSpan += System.currentTimeMillis();
            try {
                HttpEntity entity = response.getEntity();
                parse = (DBObject) JSON.parse(getGTBody(EntityUtils.toString(entity)));
                logger.trace(parse.toString());
            } finally {
                response.close();
            }

            //3 resolve deltaX
            String fullbgSrc = PRE_FIX + parse.get("fullbg");
            List<String> fullbgPositionList = POSITIONS;
            String bgSrc = PRE_FIX + parse.get("bg");
            List<String> bgPositionList = POSITIONS;
            int itemWidth = 10;
            int itemHeight = 58;
            int lineItemCount = 26;

            String tmpFolder = System.getProperty("user.dir") + "/tmp/";
            File file = new File(tmpFolder);
            boolean mflag = true;
            if (!file.exists() && !file.isDirectory())
                mflag = file.mkdir();
            if (!mflag)
                return null;
            String identification = String.valueOf(System.currentTimeMillis()) + Thread.currentThread().getId();
            String imageSubfix = "jpg";

            List<String[]> fullbgPointList = new ArrayList<>();
            for (String positionStr : fullbgPositionList) {
                fullbgPointList.add(positionStr.replace("px", "").split(" "));
            }
            List<String[]> bgPointList = new ArrayList<>();
            for (String positionStr : bgPositionList) {
                bgPointList.add(positionStr.replace("px", "").split(" "));
            }
            String fullbgImagePath = tmpFolder + identification + "_fullbg." + imageSubfix;


            String bgImagePath = tmpFolder + identification + "_bg." + imageSubfix;
            int deltaX = 0;
            if (ImageUtils.combineImages(fullbgSrc, fullbgPointList, lineItemCount, itemWidth, itemHeight, fullbgImagePath, imageSubfix)
                    && ImageUtils.combineImages(bgSrc, bgPointList, lineItemCount, itemWidth, itemHeight, bgImagePath, imageSubfix)) {
                try {
                    deltaX = ImageUtils.findXDiffRectangeOfTwoImage(fullbgImagePath, bgImagePath);
                } catch (DeltaXResolveFailException e) {
                    e.printStackTrace();
                    throw e;
                }
                deleteImage(fullbgImagePath);
                deleteImage(bgImagePath);
            }
            long start_1 = System.currentTimeMillis();
            long end_1 = System.currentTimeMillis();
            logger.debug("select cost: " + (end_1 - start_1) + "ms");
//            String trailStr = TrailGen.generateTrail(deltaX-6);
            String trailStr = TrailWeaver.getTrail(deltaX-6);

            //4 finally, get validate
            challenge = (String) parse.get("challenge");
            String s = (String) parse.get("s");
            BasicDBList c = (BasicDBList) parse.get("c");
            StringBuilder sb = new StringBuilder();
            sb.append("http://api.geetest.com/ajax.php?");
            sb.append("gt=").append(gt);
            sb.append("&").append("challenge").append("=").append(challenge);
            sb.append("&").append("userresponse").append("=").append(JSEngine.userResponse(deltaX - 6 + "", challenge));
            String passTime = getPassTime(trailStr);
            sb.append("&").append("passtime").append("=").append(passTime);
            sb.append("&").append("imgload").append("=").append(getImgLoad());
            sb.append("&").append("aa").append("=").append(JSEngine.getA(trailStr,c.toString(),s));
            sb.append("&").append("callback").append("=").append("geetest_" + System.currentTimeMillis());
            httpGet = new HttpGet(sb.toString());
            httpGet.setConfig(config);
            httpGet.setHeader("Referer", Provinces.getReferrerHtml(province));
            int sleepTime = Integer.parseInt(passTime);
            TimeUnit.MILLISECONDS.sleep(sleepTime);
            response = httpClient.execute(httpGet);

            try {
                HttpEntity entity = response.getEntity();
                parse = (DBObject) JSON.parse(getGTBody(EntityUtils.toString(entity)));
            } finally {
                response.close();
            }
            String validate = (String) parse.get("validate");
            if (validate == null) {
                logger.debug("Thread: " + threadId + "\t" + parse);
            }
            start_1 = System.currentTimeMillis();
            end_1 = System.currentTimeMillis();
            logger.debug("insert cost: " + (end_1 - start_1) + "ms");
            // 增加破解时间间隔
            TimeUnit.MILLISECONDS.sleep(crackInterval);
            boolean error = false;
            if (proxy != null) {
                error = Provinces.updateFailureContinuity(proxy.getHostName(), validate == null);
            }
            if (validate == null) {
                if (error) {
                    throw new IPLimitException();
                }
                return null;
            } else {
                DBObject result = new BasicDBObject();
                result.put("status", "ok");
                result.put("gt", gt);
                result.put("challenge", challenge);
                result.put("validate", validate);

                return result;
            }
        }
    }

    private GeetestTrail selectRandom(List<GeetestTrail> trails) {
        int size = trails.size();
        if (size == 0) {
            return null;
        }
        int index = random.nextInt(size);
        return trails.get(index);
    }


    private static String getGTBody(String output) {
        Matcher matcher = GT_BODY_PTN.matcher(output);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    private static String getPassTime(String trailStr) {
        Matcher matcher = LAST_NUM_PTN.matcher(trailStr);
        int passtime = 0;
        while (matcher.find()) {
            String group = matcher.group(1);
            passtime += Integer.parseInt(group);
        }
        return passtime + "";
    }

    private static String getImgLoad() {
        return 25 + random.nextInt(35) + "";
    }


    private void deleteImage(String fullbgImagePath) {
        File file = new File(fullbgImagePath);
        // 路径为文件且不为空则进行删除
        boolean dFlag = true;
        if (file.isFile() && file.exists()) {
            dFlag = file.delete();
        }
        if (!dFlag) {
        }
    }

    /*public static void main(String[] args) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // post参数
        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNamealuePair("tab", "ent_tab"));
        formparams.add(new BasicNameValuePair("token", ""));
        formparams.add(new BasicNameValuePair("geetest_challenge", challenge));
        formparams.add(new BasicNameValuePair("geetest_validate", validate));
        formparams.add(new BasicNameValuePair("geetest_seccode", validate+"|jordan"));
        formparams.add(new BasicNameValuePair("searchword", "中国技术创新"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);

        // 构建HttpPost对象
        HttpPost httppost = new HttpPost("http://www.gsxt.gov.cn/corp-query-search-1.html");
        httppost.setEntity(entity);
        httppost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");


        try (CloseableHttpResponse response = httpClient.execute(httppost)){
            // 得到搜索结果页
            String searchResultContent = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
