package com.prodaas.config;

import com.prodaas.constant.Provinces;
import com.prodaas.mapper.ProxyMapper;
import com.prodaas.util.JSEngine;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by guyu on 2016/10/18.
 */
@Configuration
@ComponentScan(value = "com.prodaas", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class)})
public class RootConfig
        extends WebMvcConfigurerAdapter implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(RootConfig.class);
    public static final String THREAD_TIMES = "thread_times";
    public static final String BOT_ID = "bot_id";
    public static final String USE_PROXY = "use_proxy";
    public static final String HTTP_REFERRER = "http_referrer";
    public static final String CAPTCHA_URL = "captcha_url";

    /**
     * .properties文件读取
     *
     * @return
     */
    @Bean
    public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
        PropertyPlaceholderConfigurer propertyPlaceholderConfigurer
                = new PropertyPlaceholderConfigurer();
        propertyPlaceholderConfigurer.setLocations(new Resource[]{
                new ClassPathResource("config.properties"),
        });
        return propertyPlaceholderConfigurer;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        List<Map<String, Object>> proxies = getProxies();
        JSEngine.init();
        int proxySize = proxies.size();
        if (proxySize == 0) {
            logger.error("found no proxies.");
            return;
        }


        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            // Ant-style path matching
            Resource[] resources = resolver.getResources("/province/*");

            for (Resource resource : resources) {
                String filename = resource.getFile().getName();
                String province = filename.substring(0, filename.indexOf("."));
                InputStream is = resource.getInputStream();
                Properties properties = new Properties();
                properties.load(is);
                initProvince(province, properties,proxies);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<Map<String, Object>> getProxies() {
        try {
            String resource = "mybatis-proxy-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            inputStream.close();
            SqlSession session = sqlSessionFactory.openSession();
            try {
                ProxyMapper mapper = session.getMapper(ProxyMapper.class);

                List<Map<String, Object>> result = mapper.getProxies();
                return result;
            } finally {
                session.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initProvince(String province, Properties properties, List<Map<String, Object>> proxies) {
        String threadTimes = (String) properties.get(THREAD_TIMES);
        int iThreadTimes = 1;
        try {
            iThreadTimes = Integer.parseInt(threadTimes);
        } catch (Exception e) {
        }
        String botId = (String) properties.get(BOT_ID);
        Integer iBotId;
        try {
            iBotId = Integer.parseInt(botId);
        } catch (Exception e) {
            logger.error("botId invalid",e);
            return;
        }
        Boolean useProxy = Boolean.valueOf((String) properties.get(USE_PROXY));
        Provinces.putUseProxy(province,useProxy);

        String httpReferrer = (String) properties.get(HTTP_REFERRER);
        String captchaUrl = (String) properties.get(CAPTCHA_URL);


        Provinces.putBotId(province, iBotId);
        Provinces.putProvince(province);

        BlockingQueue<HttpHost> queue = new LinkedBlockingQueue<>(proxies.size()*iThreadTimes);
        if (useProxy) {
            for (int i = 0; i < iThreadTimes; i++) {
                for (Map<String, Object> map : proxies) {
                    String host = (String) map.get("host");
                    Long port = (Long) map.get("port");
                    HttpHost proxy = new HttpHost(host, port.intValue());
                    try {
                        queue.put(proxy);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Provinces.putProxies(province, queue);
        }

        Provinces.putHttpReferrer(province, httpReferrer);
        Provinces.putCaptchaUrl(province, captchaUrl);
    }


}
